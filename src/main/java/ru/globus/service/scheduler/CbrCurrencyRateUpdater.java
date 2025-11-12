package ru.globus.service.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.globus.dto.CbrResponse;
import ru.globus.feign.BankClient;
import ru.globus.model.enums.CurrencyCode;
import ru.globus.service.CurrencyRateService;
import ru.globus.util.XmlParser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Планировщик обновления курсов валют с сайта ЦБР.
 * Работает только в профиле "real-scheduler".
 */
@Slf4j
@Service
@Profile("real-scheduler")
@RequiredArgsConstructor
public class CbrCurrencyRateUpdater implements CurrencyRateUpdater{

    private final BankClient bankClient;
    private final CurrencyRateService currencyRateService;

    /**
     * Выполняет обновление курсов при запуске приложения.
     */
    @PostConstruct
    public void init() {
        log.info("Инициализация: обновление курсов валют при запуске");
        updateRates();
    }

    /**
     * Обновляет курсы валют по расписанию (ежедневно в 03:00).
     */
    @Override
    @Scheduled(cron = "0 0 3 * * *")
    public void updateRates() {
        log.info("Запуск обновления курсов валют ЦБР");

        try {
            String xml = bankClient.getDailyRates();

            CbrResponse response = XmlParser.parseCbrXml(xml);

            Map<CurrencyCode, BigDecimal> rates = response.getCurrencies().stream()
                .collect(Collectors.toMap(
                    currency -> CurrencyCode.fromCode(currency.getCode()),
                    currency -> new BigDecimal(currency.getValue().replace(",","."))
                ));

            rates.put(CurrencyCode.RUB, BigDecimal.ONE);

            currencyRateService.saveRates(rates, LocalDate.now());

            log.info("Обновление курса валют завершено. Загружено {} записей", rates.size());
        } catch (Exception e) {
            log.error("Ошибка при обновлении курсов валют ЦБР: {}", e.getMessage(), e);
        }
    }
}

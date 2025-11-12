package ru.globus.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.globus.exception.ValidationException;
import ru.globus.model.entity.CurrencyRate;
import ru.globus.model.enums.CurrencyCode;
import ru.globus.repository.CurrencyRateRepository;
import ru.globus.service.CurrencyRateService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

/**
 * Имплементация CurrencyRateService для управления пользователями.
 * Определяет основные операции CRUD и бизнес-логику, связанную с пользователями.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateServiceImpl implements CurrencyRateService {
    private final CurrencyRateRepository currencyRateRepository;

    /*
     * Метод сохранения курсов валют в базу
     */
    @Override
    public void saveRates(Map<CurrencyCode, BigDecimal> rates, LocalDate date) {
        rates.forEach((code, newRateValue) -> {
            currencyRateRepository.findTopByCurrencyCodeOrderByRateDateDesc(code)
                    .ifPresentOrElse(
                            existing -> {
                                if (existing.getRateDate().isEqual(date) &&
                                        existing.getRate().compareTo(newRateValue) == 0) {
                                    log.debug("Актуальный курс {} уже сохранён: {} за {}", code, newRateValue, date);
                                } else {
                                    CurrencyRate updated = CurrencyRate.builder()
                                            .currencyCode(code)
                                            .rate(newRateValue)
                                            .rateDate(date)
                                            .build();
                                    currencyRateRepository.save(updated);
                                    log.info("Обновлён курс {} = {} за {}", code, newRateValue, date);
                                }
                            },
                            () -> {
                                CurrencyRate created = CurrencyRate.builder()
                                        .currencyCode(code)
                                        .rate(newRateValue)
                                        .rateDate(date)
                                        .build();
                                currencyRateRepository.save(created);
                                log.info("Добавлен новый курс {} = {} за {}", code, newRateValue, date);
                            }
                    );
        });
    }


    @Override
    public BigDecimal getConversionRate(CurrencyCode from, CurrencyCode to, LocalDate date) {
        if (from == to) return BigDecimal.ONE;

        LocalDate yesterday = date.minusDays(1);

        var rateFrom = currencyRateRepository.findByCurrencyCodeAndRateDate(from, date)
            .or(() -> currencyRateRepository.findByCurrencyCodeAndRateDate(from, yesterday))
            .orElseThrow(() -> new ValidationException("Курс для валюты " + from + " не найден за " + date + " и " + yesterday));

        var rateTo = currencyRateRepository.findByCurrencyCodeAndRateDate(to, date)
            .or(() -> currencyRateRepository.findByCurrencyCodeAndRateDate(to, yesterday))
            .orElseThrow(() -> new ValidationException("Курс для валюты " + to + " не найден за " + date + " и " + yesterday));

        return rateFrom.getRate().divide(rateTo.getRate(), 6, RoundingMode.HALF_UP);
    }
}

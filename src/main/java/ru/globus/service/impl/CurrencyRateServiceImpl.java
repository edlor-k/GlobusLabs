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
        rates.forEach((code, value) -> currencyRateRepository.findByCurrencyCodeAndRateDate(code, date)
                .ifPresentOrElse(
                    existing -> log.debug("Курс {} за {} уже существует", code, date),
                    () -> {
                        CurrencyRate rate = CurrencyRate.builder()
                            .currencyCode(code)
                            .rate(value)
                            .rateDate(date)
                            .build();
                        currencyRateRepository.save(rate);
                        log.info("Добавлен курс {} = {} за {}", code, value, date);
                    }
                ));
    }

    @Override
    public BigDecimal getConversionRate(CurrencyCode from, CurrencyCode to, LocalDate date) {
        if (from == to) {
            return BigDecimal.ONE;
        }
        var rateFrom = currencyRateRepository.findByCurrencyCodeAndRateDate(from, date)
            .orElseThrow(() -> new ValidationException("Курс для валюты " + from + " на дату " + date + " не найден"));
        var rateTo = currencyRateRepository.findByCurrencyCodeAndRateDate(to, date)
            .orElseThrow(() -> new ValidationException("Курс для валюты " + to + " на дату " + date + " не найден"));
        return rateTo.getRate().divide(rateFrom.getRate(), 6, RoundingMode.HALF_UP);
    }
}

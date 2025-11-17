package ru.globus.service;

import ru.globus.model.enums.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Сервисный интерфейс для работы с сущностью CurrencyRate.
 * Содержит метод сохранения курсов в базу.
 */
public interface CurrencyRateService {

    /**
     * Метод сохранения курсов валют в базу
     */
    void saveRates(Map<CurrencyCode, BigDecimal> rates, LocalDate date);

    /**
     * Получить коэффициент конвертации между двумя валютами на указанную дату.
     * Если валюты одинаковые, возвращает 1.
     * Если курс не найден, выбрасывает исключение.
     *
     * @param from валюта отправителя
     * @param to валюта получателя
     * @param date дата курса
     * @return коэффициент конвертации (сколько единиц to за одну единицу from)
     */
    BigDecimal getConversionRate(CurrencyCode from, CurrencyCode to, LocalDate date);
}

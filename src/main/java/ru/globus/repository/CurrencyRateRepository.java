package ru.globus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.globus.model.entity.CurrencyRate;
import ru.globus.model.enums.CurrencyCode;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Репозиторий для работы с курсами валют.
 *
 * @author Vladlen Korablev
 */
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {

    /**
     * Ищет курс валюты за указанную дату.
     */
    Optional<CurrencyRate> findByCurrencyCodeAndRateDate(CurrencyCode code, LocalDate date);

    /**
     * Возвращает последний (самый свежий) курс валюты.
     */
    Optional<CurrencyRate> findTopByCurrencyCodeOrderByRateDateDesc(CurrencyCode code);
}

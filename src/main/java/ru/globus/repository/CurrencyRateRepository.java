package ru.globus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.globus.model.entity.CurrencyRate;
import ru.globus.model.enums.CurrencyCode;

import java.time.LocalDate;
import java.util.Optional;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {
    Optional<CurrencyRate> findByCurrencyCodeAndRateDate(CurrencyCode code, LocalDate date);
}

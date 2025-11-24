package ru.globus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.globus.model.enums.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для событий банковского счета, передаваемых через Kafka.
 *
 * @author Vladlen Korablev
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountEventDto {

    /**
     * Тип события.
     */
    private EventType eventType;

    /**
     * ID банковского счета.
     */
    private UUID accountId;

    /**
     * ID пользователя-владельца счета.
     */
    private UUID userId;

    /**
     * Баланс счета.
     */
    private BigDecimal balance;

    /**
     * Валюта счета.
     */
    private CurrencyCode currency;

    /**
     * Время события.
     */
    private LocalDateTime timestamp;

    /**
     * Дополнительная информация.
     */
    private String message;

    /**
     * Типы событий банковского счета.
     */
    public enum EventType {
        ACCOUNT_CREATED,
        ACCOUNT_UPDATED,
        ACCOUNT_DELETED,
        BALANCE_CHANGED,
        TRANSFER_COMPLETED
    }
}

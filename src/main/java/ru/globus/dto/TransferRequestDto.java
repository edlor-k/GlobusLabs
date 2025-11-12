package ru.globus.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO для запроса перевода средств между счетами пользователя.
 *
 * @author Vladlen Korablev
 */
public record TransferRequestDto(
    @NotNull(message = "ID счета отправителя обязателен")
    UUID fromAccountId,

    @NotNull(message = "ID счета получателя обязателен")
    UUID toAccountId,

    @NotNull(message = "Сумма перевода обязательна")
    @Positive(message = "Сумма перевода должна быть положительной")
    BigDecimal amount
) {}

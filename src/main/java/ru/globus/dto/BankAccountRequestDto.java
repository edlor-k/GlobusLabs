package ru.globus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ru.globus.model.enums.CurrencyCode;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO для создания или обновления банковского счёта.
 *
 * @author Vladlen Korablev
 */
@Schema(description = "Запрос на создание или обновление банковского счёта")
public record BankAccountRequestDto(

    @Schema(
        description = "Идентификатор владельца счёта (пользователя)",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    )
    UUID userId,

    @NotNull(message = "Не указана валюта счёта")
    @Schema(
        description = "Код валюты счёта",
        example = "USD",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    CurrencyCode currencyCode,

    @NotBlank(message = "Номер счёта не может быть пустым")
    @Schema(
        description = "Номер банковского счёта",
        example = "40817810099910004321",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String accountNumber,

    @PositiveOrZero(message = "Баланс не может быть отрицательным")
    @Schema(
        description = "Начальный баланс счёта (может быть 0)",
        example = "1000.00",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    BigDecimal balance
) {}

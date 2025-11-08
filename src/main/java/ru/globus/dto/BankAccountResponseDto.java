package ru.globus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.globus.model.enums.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO, представляющий банковский счёт в ответах API.
 */
@Schema(description = "Ответ с информацией о банковском счёте")
public record BankAccountResponseDto(

    @Schema(
        description = "Идентификатор банковского счёта",
        example = "4a2e0b3c-b41d-4d6e-b5d9-b7f3cd0c33a4"
    )
    UUID id,

    @Schema(
        description = "Идентификатор пользователя-владельца счёта",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    )
    UUID userId,

    @Schema(
        description = "Код валюты счёта",
        example = "USD"
    )
    CurrencyCode currencyCode,

    @Schema(
        description = "Номер банковского счёта",
        example = "40817810099910004321"
    )
    String accountNumber,

    @Schema(
        description = "Текущий баланс счёта",
        example = "1500.00"
    )
    BigDecimal balance,

    @Schema(
        description = "Флаг активности счёта (true — активен, false — закрыт)",
        example = "true"
    )
    Boolean active,

    @Schema(
        description = "Дата и время создания счёта",
        example = "2025-11-07T14:23:55"
    )
    LocalDateTime createdAt
) {}

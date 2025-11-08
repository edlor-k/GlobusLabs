package ru.globus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO-ответ с данными о пользователе.
 */
@Schema(description = "Ответ с информацией о пользователе")
public record UserResponseDto(

    @Schema(
        description = "Уникальный идентификатор пользователя",
        example = "b1b2a9c0-3b2f-4b72-a9b1-ccf17d345678"
    )
    UUID id,

    @Schema(
        description = "Email пользователя",
        example = "user@example.com"
    )
    String email,

    @Schema(
        description = "Имя пользователя",
        example = "Иван"
    )
    String firstname,

    @Schema(
        description = "Фамилия пользователя",
        example = "Иванов"
    )
    String surname,

    @Schema(
        description = "Отчество пользователя (если указано)",
        example = "Иванович"
    )
    String middlename,

    @Schema(
        description = "Дата и время регистрации пользователя",
        example = "2025-11-07T14:23:55"
    )
    LocalDateTime registeredAt,

    @Schema(
        description = "Список идентификаторов банковских счетов, принадлежащих пользователю",
        example = "[\"4a2e0b3c-b41d-4d6e-b5d9-b7f3cd0c33a4\", \"7b3c5d2e-22f1-4e7a-9c0b-88219f1211ab\"]"
    )
    List<UUID> accountIds
) {}

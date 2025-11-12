package ru.globus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.globus.model.entity.User;

/**
 * DTO-запрос для создания или обновления пользователя.
 *
 * @author Vladlen Korablev
 */
@Schema(description = "Запрос на создание или обновление пользователя")
public record UserRequestDto(

    @NotNull(message = "Email должен быть указан")
    @Email(message = "Некорректный формат email")
    @Schema(
        description = "Адрес электронной почты пользователя",
        example = "user@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String email,

    @Size(message = "Имя пользователя должно содержать от 2 до 50 символов", min = 2, max = 50)
    @NotBlank(message = "Имя пользователя должно быть указано")
    @Schema(
        description = "Имя пользователя",
        example = "Иван",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String firstname,

    @Size(message = "Фамилия пользователя должна содержать от 2 до 50 символов", min = 2, max = 50)
    @NotBlank(message = "Фамилия должна быть указана")
    @Schema(
        description = "Фамилия пользователя",
        example = "Иванов",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String surname,

    @Size(message = "Отчество должно содержать от 2 до 50 символов", min = 2, max = 50)
    @Schema(
        description = "Отчество пользователя (необязательно)",
        example = "Иванович",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    String middlename
) {}

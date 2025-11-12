package ru.globus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.globus.dto.UserRequestDto;
import ru.globus.dto.UserResponseDto;
import ru.globus.service.UserService;

import java.util.UUID;

/**
 * Контроллер для управления пользователями.
 *
 * @author Vladlen Korablev
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "Операции по управлению пользователями")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Получить всех пользователей (с постраничной пагинацией).
     */
    @GetMapping
    @Operation(summary = "Получить всех пользователей с пагинацией")
    @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен")
    public Page<UserResponseDto> getAll(@ParameterObject Pageable pageable) {
        return userService.getAll(pageable);
    }

    /**
     * Создать нового пользователя.
     */
    @PostMapping
    @Operation(summary = "Создать нового пользователя")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody UserRequestDto dto) {
        return userService.create(dto);
    }

    /**
     * Найти пользователя по ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID")
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    public UserResponseDto findUser(@PathVariable UUID id) {
        return userService.getById(id);
    }

    /**
     * Обновить данные пользователя по ID.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные пользователя")
    @ApiResponse(responseCode = "200", description = "Данные успешно обновлены")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content)
    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateUser(
        @PathVariable UUID id,
        @Valid @RequestBody UserRequestDto dto
    ) {
        return userService.update(id, dto);
    }

    /**
     * Удалить пользователя по ID.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя по ID")
    @ApiResponse(responseCode = "204", description = "Пользователь успешно удалён")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        userService.delete(id);
    }
}

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
import ru.globus.dto.BankAccountRequestDto;
import ru.globus.dto.BankAccountResponseDto;
import ru.globus.dto.TransferRequestDto;
import ru.globus.service.BankAccountService;

import java.util.UUID;

/**
 * Контроллер для управления банковскими счетами.
 *
 * @author Vladlen Korablev
 */
@RestController
@RequestMapping("/accounts")
@Tag(name = "Банковские счета", description = "Операции по управлению банковскими счетами пользователей")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    /**
     * Получить список всех счетов (с пагинацией).
     */
    @GetMapping
    @Operation(summary = "Получить все банковские счета с пагинацией")
    @ApiResponse(responseCode = "200", description = "Список счетов успешно получен")
    public Page<BankAccountResponseDto> getAll(@ParameterObject Pageable pageable) {
        return bankAccountService.getAll(pageable);
    }

    /**
     * Создать новый банковский счёт.
     */
    @PostMapping
    @Operation(summary = "Создать новый банковский счёт")
    @ApiResponse(responseCode = "201", description = "Банковский счёт успешно создан")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content)
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccountResponseDto createAccount(@Valid @RequestBody BankAccountRequestDto dto) {
        return bankAccountService.create(dto);
    }

    /**
     * Получить банковский счёт по ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получить банковский счёт по ID")
    @ApiResponse(responseCode = "200", description = "Счёт найден")
    @ApiResponse(responseCode = "404", description = "Счёт не найден", content = @Content)
    public BankAccountResponseDto getAccountById(@PathVariable UUID id) {
        return bankAccountService.getById(id);
    }

    /**
     * Обновить данные банковского счёта по ID.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные банковского счёта")
    @ApiResponse(responseCode = "200", description = "Данные успешно обновлены")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content)
    @ApiResponse(responseCode = "404", description = "Счёт не найден", content = @Content)
    @ResponseStatus(HttpStatus.OK)
    public BankAccountResponseDto updateAccount(
        @PathVariable UUID id,
        @Valid @RequestBody BankAccountRequestDto dto
    ) {
        return bankAccountService.update(id, dto);
    }

    /**
     * Удалить банковский счёт по ID.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить банковский счёт по ID")
    @ApiResponse(responseCode = "204", description = "Счёт успешно удалён")
    @ApiResponse(responseCode = "404", description = "Счёт не найден", content = @Content)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable UUID id) {
        bankAccountService.delete(id);
    }

    /**
     * Перевести средства между счетами.
     */
    @PostMapping("/transfer")
    @Operation(summary = "Перевести средства между банковскими счетами")
    @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации или недостаточно средств", content = @Content)
    @ApiResponse(responseCode = "404", description = "Один из счетов не найден", content = @Content)
    @ResponseStatus(HttpStatus.OK)
    public void transferFunds(@Valid @RequestBody TransferRequestDto dto) {
        bankAccountService.transfer(dto);
    }
}

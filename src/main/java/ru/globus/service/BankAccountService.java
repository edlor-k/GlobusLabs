package ru.globus.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.globus.dto.BankAccountRequestDto;
import ru.globus.dto.BankAccountResponseDto;
import ru.globus.dto.TransferRequestDto;

import java.util.UUID;

/**
 * Сервисный интерфейс для управления банковскими счетами пользователей.
 * Определяет CRUD-операции и бизнес-логику, связанную с банковскими счетами.
 */
public interface BankAccountService {

    /**
     * Создаёт новый банковский счёт для пользователя.
     *
     * @param dto DTO с данными для создания счёта
     * @return DTO с информацией о созданном счёте
     */
    BankAccountResponseDto create(BankAccountRequestDto dto);

    /**
     * Возвращает все счета с постраничной пагинацией.
     *
     * @param pageable объект пагинации
     * @return страница DTO счетов
     */
    Page<BankAccountResponseDto> getAll(Pageable pageable);

    /**
     * Возвращает счёт по его идентификатору.
     *
     * @param id идентификатор счёта
     * @return DTO с информацией о найденном счёте
     */
    BankAccountResponseDto getById(UUID id);

    /**
     * Обновляет данные существующего счёта.
     *
     * @param id  идентификатор счёта
     * @param dto DTO с обновлёнными данными
     * @return DTO обновлённого счёта
     */
    BankAccountResponseDto update(UUID id, BankAccountRequestDto dto);

    /**
     * Удаляет банковский счёт по идентификатору.
     *
     * @param id идентификатор счёта
     */
    void delete(UUID id);

    /**
     * Переводит средства между счетами пользователя с конвертацией валют по текущему курсу.
     *
     * @param dto запрос на перевод
     */
    void transfer(TransferRequestDto dto);
}

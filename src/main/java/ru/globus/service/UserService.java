package ru.globus.service;

import ru.globus.dto.UserRequestDto;
import ru.globus.dto.UserResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

/**
 * Сервисный интерфейс для работы с пользователями.
 * Определяет основные операции CRUD и бизнес-логику, связанную с пользователями.
 *
 * @author Vladlen Korablev
 */
public interface UserService {

    /**
     * Создаёт нового пользователя на основе данных из DTO.
     *
     * @param dto DTO с данными нового пользователя
     * @return DTO с сохранённым пользователем
     */
    UserResponseDto create(UserRequestDto dto);

    /**
     * Возвращает список всех пользователей.
     *
     * @return список DTO с информацией о пользователях
     */
    Page<UserResponseDto> getAll(Pageable pageable);

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return DTO с данными найденного пользователя
     */
    UserResponseDto getById(UUID id);

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param id  идентификатор пользователя
     * @param dto DTO с новыми данными
     * @return обновлённый DTO пользователя
     */
    UserResponseDto update(UUID id, UserRequestDto dto);

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     */
    void delete(UUID id);
}

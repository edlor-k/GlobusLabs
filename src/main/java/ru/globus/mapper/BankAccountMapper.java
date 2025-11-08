package ru.globus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.globus.dto.BankAccountRequestDto;
import ru.globus.dto.BankAccountResponseDto;
import ru.globus.model.entity.BankAccount;
import ru.globus.model.entity.User;

/**
 * Маппер для преобразования между сущностью BankAccount и DTO-классами.
 * Используется библиотека MapStruct, реализация создаётся автоматически при компиляции.
 */
@Mapper(componentModel = "spring")
public interface BankAccountMapper {

    /**
     * Преобразует DTO-запрос в сущность BankAccount.
     *
     * Пользователь передаётся отдельно, так как в DTO хранится только его идентификатор.
     * При создании сущности:
     * - поле id игнорируется (генерируется базой данных);
     * - поле active устанавливается в true;
     * - поле createdAt заполняется текущим временем.
     *
     * @param dto DTO с данными для создания банковского счёта
     * @param user сущность пользователя (владелец счёта)
     * @return новая сущность BankAccount
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    BankAccount toEntity(BankAccountRequestDto dto, User user);

    /**
     * Преобразует сущность BankAccount в DTO-ответ.
     * В DTO сохраняются только основные поля, включая идентификатор пользователя.
     *
     * @param entity сущность банковского счёта
     * @return DTO с данными для возврата клиенту
     */
    @Mapping(target = "userId", source = "user.id")
    BankAccountResponseDto toResponseDto(BankAccount entity);
}

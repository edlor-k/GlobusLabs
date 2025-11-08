package ru.globus.mapper;

import org.mapstruct.*;
import ru.globus.dto.UserRequestDto;
import ru.globus.dto.UserResponseDto;
import ru.globus.model.entity.BankAccount;
import ru.globus.model.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * Маппер для преобразования между сущностью User
 * и DTO-классами UserRequestDto и UserResponseDto.
 * Используется библиотека MapStruct, реализация генерируется во время компиляции.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Преобразует DTO-запрос UserRequestDto в сущность User.
     *
     * При создании пользователя:
     * - поле id игнорируется (генерируется базой данных)
     * - поле registeredAt заполняется текущим временем
     * - список accounts игнорируется
     *
     * @param userRequestDto DTO с входными данными пользователя
     * @return новая сущность User
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registeredAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "accounts", ignore = true)
    User toEntity(UserRequestDto userRequestDto);

    /**
     * Преобразует сущность User в DTO-ответ UserResponseDto.
     * В объекте DTO вместо списка счетов передаются только их идентификаторы.
     *
     * @param user сущность пользователя
     * @return DTO с основной информацией о пользователе
     */
    @Mapping(target = "accountIds", expression = "java(accountsToAccountIds(user.getAccounts()))")
    UserResponseDto toResponseDto(User user);

    /**
     * Обновляет существующую сущность User на основе данных из DTO.
     *
     * Игнорирует поля, которые не должны изменяться:
     * - id
     * - registeredAt
     * - accounts
     *
     * Не перезаписывает поля, если в DTO пришёл null.
     *
     * @param dto  DTO с новыми данными пользователя
     * @param user сущность, которую нужно обновить
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    void updateEntityFromDto(UserRequestDto dto, @MappingTarget User user);

    /**
     * Преобразует список банковских счетов в список их идентификаторов.
     * Если список счетов пустой или равен null, возвращается пустой список.
     *
     * @param accounts список банковских счетов пользователя
     * @return список идентификаторов счетов
     */
    default List<UUID> accountsToAccountIds(List<BankAccount> accounts) {
        return accounts == null
            ? List.of()
            : accounts.stream()
            .map(BankAccount::getId)
            .toList();
    }
}

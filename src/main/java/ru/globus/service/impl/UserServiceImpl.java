package ru.globus.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.globus.aop.LogMethod;
import ru.globus.dto.UserRequestDto;
import ru.globus.dto.UserResponseDto;
import ru.globus.exception.UserAlreadyExistException;
import ru.globus.exception.UserNotFoundException;
import ru.globus.mapper.UserMapper;
import ru.globus.repository.UserRepository;
import ru.globus.service.UserService;

import java.util.UUID;

/**
 * Имплементация UserService для управления пользователями.
 * Определяет основные операции CRUD и бизнес-логику, связанную с пользователями.
 *
 * @author Vladlen Korablev
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    /**
     * Создаёт нового пользователя на основе данных из DTO.
     *
     * @param dto DTO с данными нового пользователя
     * @return DTO с сохранённым пользователем
     */
    @Override
    @LogMethod("user-create")
    public UserResponseDto create(UserRequestDto dto) {
        var user = userMapper.toEntity(dto);
        if (userRepository.existsByEmail(dto.email())) {
            log.error("Пользователь с email {} уже существует", dto.email());
            throw new UserAlreadyExistException("Пользователь с email " + dto.email() + " уже существует");
        }
        var saved = userRepository.save(user);
        return userMapper.toResponseDto(saved);
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список DTO с информацией о пользователях
     */
    @Override
    @LogMethod("user-get-all")
    public Page<UserResponseDto> getAll(Pageable pageable) {
        return userRepository
            .findAll(pageable)
            .map(userMapper::toResponseDto);
    }

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return DTO с данными найденного пользователя
     */
    @Override
    @LogMethod("user-get-id")
    public UserResponseDto getById(UUID id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + id));
        return userMapper.toResponseDto(user);
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param id  идентификатор пользователя
     * @param dto DTO с новыми данными
     * @return обновлённый DTO пользователя
     */
    @Override
    @LogMethod("user-update")
    public UserResponseDto update(UUID id, UserRequestDto dto) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + id));
        userMapper.updateEntityFromDto(dto, user);
        var updated = userRepository.save(user);
        return userMapper.toResponseDto(updated);
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     */
    @Override
    @LogMethod("user-delete")
    public void delete(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("Пользователь {} успешно удалён", id);
        } else {
            log.warn("Попытка удалить несуществующего пользователя {}", id);
        }
    }
}

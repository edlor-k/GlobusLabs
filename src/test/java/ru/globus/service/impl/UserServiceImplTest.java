package ru.globus.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.globus.dto.UserRequestDto;
import ru.globus.dto.UserResponseDto;
import ru.globus.exception.UserAlreadyExistException;
import ru.globus.exception.UserNotFoundException;
import ru.globus.mapper.UserMapper;
import ru.globus.model.entity.User;
import ru.globus.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User user;
    private UserRequestDto requestDto;
    private UserResponseDto responseDto;
    private LocalDateTime registeredAt;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        registeredAt = LocalDateTime.now();

        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setFirstname("Иван");
        user.setSurname("Иванов");
        user.setMiddlename("Иванович");
        user.setRegisteredAt(registeredAt);

        requestDto = new UserRequestDto("test@example.com", "Иван", "Иванов", "Иванович");
        responseDto = new UserResponseDto(userId, "test@example.com", "Иван", "Иванов", "Иванович", registeredAt, List.of());
    }

    @Test
    void create_ShouldCreateUser_WhenValidData() {
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);
        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.create(requestDto);

        assertEquals(responseDto, result);
        verify(userRepository).existsByEmail(requestDto.email());
        verify(userRepository).save(user);
    }

    @Test
    void create_ShouldThrowUserAlreadyExistException_WhenEmailExists() {
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> userService.create(requestDto));
    }

    @Test
    void getAll_ShouldReturnPagedUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(page);
        when(userMapper.toResponseDto(user)).thenReturn(responseDto);

        Page<UserResponseDto> result = userService.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void getById_ShouldReturnUser_WhenExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.getById(userId);

        assertEquals(responseDto, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void getById_ShouldThrowUserNotFoundException_WhenNotExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(userId));
    }

    @Test
    void update_ShouldUpdateUser_WhenExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.update(userId, requestDto);

        assertEquals(responseDto, result);
        verify(userRepository).findById(userId);
        verify(userMapper).updateEntityFromDto(requestDto, user);
        verify(userRepository).save(user);
    }

    @Test
    void update_ShouldThrowUserNotFoundException_WhenNotExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(userId, requestDto));
    }

    @Test
    void delete_ShouldDeleteUser_WhenExists() {
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.delete(userId);

        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void delete_ShouldNotDelete_WhenNotExists() {
        when(userRepository.existsById(userId)).thenReturn(false);

        userService.delete(userId);

        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(userId);
    }
}

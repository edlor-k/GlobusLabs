package ru.globus.controller;

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
import ru.globus.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final UUID userId = UUID.randomUUID();
    private final UserRequestDto requestDto = new UserRequestDto("test@example.com", "John", "Doe", "Middle");
    private final UserResponseDto responseDto = new UserResponseDto(
        userId, "test@example.com", "John", "Doe", "Middle", LocalDateTime.now(), List.of()
    );

    @Test
    void getAll_shouldReturnPageOfUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        List<UserResponseDto> users = List.of(responseDto);
        Page<UserResponseDto> page = new PageImpl<>(users, pageable, 1);
        when(userService.getAll(pageable)).thenReturn(page);

        Page<UserResponseDto> result = userController.getAll(pageable);

        assertEquals(page, result);
        verify(userService).getAll(pageable);
    }

    @Test
    void createUser_shouldReturnCreatedUser() {
        when(userService.create(requestDto)).thenReturn(responseDto);

        UserResponseDto result = userController.createUser(requestDto);

        assertEquals(responseDto, result);
        verify(userService).create(requestDto);
    }

    @Test
    void createUser_shouldThrowUserAlreadyExistException() {
        when(userService.create(requestDto)).thenThrow(new UserAlreadyExistException("User exists"));

        assertThrows(UserAlreadyExistException.class, () -> userController.createUser(requestDto));

        verify(userService).create(requestDto);
    }

    @Test
    void findUser_shouldReturnUser() {
        when(userService.getById(userId)).thenReturn(responseDto);

        UserResponseDto result = userController.findUser(userId);

        assertEquals(responseDto, result);
        verify(userService).getById(userId);
    }

    @Test
    void findUser_shouldThrowUserNotFoundException() {
        when(userService.getById(userId)).thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> userController.findUser(userId));

        verify(userService).getById(userId);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        when(userService.update(userId, requestDto)).thenReturn(responseDto);

        UserResponseDto result = userController.updateUser(userId, requestDto);

        assertEquals(responseDto, result);
        verify(userService).update(userId, requestDto);
    }

    @Test
    void updateUser_shouldThrowUserNotFoundException() {
        when(userService.update(userId, requestDto)).thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> userController.updateUser(userId, requestDto));

        verify(userService).update(userId, requestDto);
    }

    @Test
    void deleteUser_shouldCallDelete() {
        userController.deleteUser(userId);

        verify(userService).delete(userId);
    }
}

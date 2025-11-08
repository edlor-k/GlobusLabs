package ru.globus.exception;

/**
 * Исключение для случая дублирования email.
 */
public class UserAlreadyExistException extends RuntimeException {

    /**
     * Создает исключение с сообщением.
     *
     * @param message сообщение об ошибке
     */
    public UserAlreadyExistException(String message) {
        super(message);
    }
}

package ru.globus.exception;

/**
 * Исключение для случая, когда пользователь не найден.
 *
 * @author Vladlen Korablev
 */
public class UserNotFoundException extends RuntimeException{

    /**
     * Создает исключение с сообщением.
     *
     * @param message сообщение об ошибке
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}

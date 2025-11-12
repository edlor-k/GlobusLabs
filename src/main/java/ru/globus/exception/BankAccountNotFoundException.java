package ru.globus.exception;

/**
 * Исключение для случая, когда пользователь не найден.
 *
 * @author Vladlen Korablev
 */
public class BankAccountNotFoundException extends RuntimeException {

    /**
     * Создает исключение с сообщением.
     *
     * @param message сообщение об ошибке
     */
    public BankAccountNotFoundException(String message){
        super(message);
    }
}

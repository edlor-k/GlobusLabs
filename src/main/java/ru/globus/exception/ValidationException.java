package ru.globus.exception;

import lombok.Getter;

import java.util.Map;

/** Бизнес-валидация входных данных (возвращаем 400).
 *
 * @author Vladlen Korablev
 * */
@Getter
public class ValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = null;
    }

    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
}

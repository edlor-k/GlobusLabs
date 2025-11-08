package ru.globus.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.globus.controller.GlobalExceptionHandler;

import java.util.Map;

/**
 * Тело ответа при обработке исключений.
 * Используется в {@link GlobalExceptionHandler}.
 */
@Data
@AllArgsConstructor
@Builder
public class ExceptionBody {
    private String message;
    private Map<String, String> errors;

    public ExceptionBody(String message) {
        this.message = message;
    }
}

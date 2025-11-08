package ru.globus.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.globus.exception.*;

/**
 * Глобальный обработчик ошибок приложения.
 * Обеспечивает централизованную обработку исключений и возврат понятных HTTP-ответов.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Ошибки бизнес-валидации (например, некорректная логика в сервисах).
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleBusinessValidation(ValidationException ex) {
        log.warn("Validation failed: {}", ex.getMessage());
        return ExceptionBody.builder()
            .message(ex.getMessage())
            .errors(ex.getErrors())
            .build();
    }

    /**
     * Ошибки, связанные с отсутствием сущностей.
     * Возвращаем статус 404, а не 400 (так логичнее и RESTful).
     */
    @ExceptionHandler({UserNotFoundException.class, BankAccountNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionBody handleNotFound(final RuntimeException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return ExceptionBody.builder()
            .message(ex.getMessage())
            .build();
    }

    /**
     * Ошибка при создании уже существующего пользователя или счёта.
     * Возвращаем 409 Conflict, как требует REST-спецификация.
     */
    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionBody handleConflict(final UserAlreadyExistException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return ExceptionBody.builder()
            .message(ex.getMessage())
            .build();
    }

    /**
     * Ошибки валидации тела запроса (аннотации @Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleValidation(final MethodArgumentNotValidException e) {
        FieldError fe = e.getBindingResult().getFieldError();
        String msg = (fe != null)
            ? fe.getField() + ": " + fe.getDefaultMessage()
            : "Validation failed";
        log.debug("Validation failed: {}", msg);
        return ExceptionBody.builder()
            .message(msg)
            .build();
    }

    /**
     * Ошибка несовпадения типов параметров (например, UUID → String).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleTypeMismatch(final MethodArgumentTypeMismatchException e) {
        String required = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown";
        String value = String.valueOf(e.getValue());
        String msg = "Параметр '" + e.getName() + "' имеет некорректное значение '" + value + "' (ожидается: " + required + ")";
        log.warn("Type mismatch: {}", msg);
        return ExceptionBody.builder()
            .message(msg)
            .build();
    }

    /**
     * Ошибки неверных аргументов, проброшенных из сервисов.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleIllegalArgument(final IllegalArgumentException e) {
        log.warn("Bad request: {}", e.getMessage());
        return ExceptionBody.builder()
            .message(e.getMessage())
            .build();
    }

    /**
     * Ошибка при парсинге XML от внешнего источника (например, ЦБР).
     * Возвращаем 502 Bad Gateway — т.к. проблема с внешним сервисом.
     */
    @ExceptionHandler(XmlParsingException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ExceptionBody handleXmlParsing(final XmlParsingException e) {
        log.error("Ошибка парсинга XML: {}", e.getMessage());
        return ExceptionBody.builder()
            .message("Ошибка при обработке данных от внешнего сервиса: " + e.getMessage())
            .build();
    }

    /**
     * Все прочие неожиданные ошибки (500 Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleUnexpected(final Exception e) {
        log.error("Unexpected error", e);
        return ExceptionBody.builder()
            .message("Внутренняя ошибка сервера. Обратитесь к администратору.")
            .build();
    }
}

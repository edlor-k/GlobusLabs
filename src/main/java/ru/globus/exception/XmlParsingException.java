package ru.globus.exception;

/**
 * Ошибка при парсинге XML
 *
 * @author Vladlen Korablev
 */
public class XmlParsingException extends RuntimeException {
    public XmlParsingException(String message) {
        super(message);
    }
}

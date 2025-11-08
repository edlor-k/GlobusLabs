package ru.globus.exception;

/**
 * Ошибка при парсинге XML
 */
public class XmlParsingException extends RuntimeException {
    public XmlParsingException(String message) {
        super(message);
    }
}

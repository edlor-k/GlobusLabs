package ru.globus.util;

import lombok.extern.slf4j.Slf4j;
import ru.globus.dto.CbrResponse;
import ru.globus.exception.XmlParsingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

/**
 * Утилита для парсинга XML-ответа от ЦБР в объект {@link CbrResponse}.
 */
@Slf4j
public final class XmlParser {

    private static final JAXBContext CONTEXT;

    static {
        try {
            CONTEXT = JAXBContext.newInstance(CbrResponse.class);
        } catch (JAXBException e) {
            throw new ExceptionInInitializerError("Ошибка инициализации JAXBContext: " + e.getMessage());
        }
    }

    public static CbrResponse parseCbrXml(String xml) {
        try {
            Unmarshaller unmarshaller = CONTEXT.createUnmarshaller();
            return (CbrResponse) unmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            log.error("Ошибка при парсинге XML от ЦБР: {}", e.getMessage());
            throw new XmlParsingException("Ошибка обработки данных от ЦБР");
        }
    }
}

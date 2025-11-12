package ru.globus.util;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import lombok.extern.slf4j.Slf4j;
import ru.globus.dto.CbrResponse;
import ru.globus.exception.XmlParsingException;

/**
 * Универсальный XML-парсер, поддерживающий JAXB-аннотации.
 */
@Slf4j
public final class XmlParser {

    private static final XmlMapper XML_MAPPER;

    static {
        XML_MAPPER = new XmlMapper();
        XML_MAPPER.registerModule(new JaxbAnnotationModule());
    }

    private XmlParser() {}

    public static CbrResponse parseCbrXml(String xml) {
        try {
            return XML_MAPPER.readValue(xml, CbrResponse.class);
        } catch (Exception e) {
            log.error("Ошибка при парсинге XML от ЦБР: {}", e.getMessage(), e);
            throw new XmlParsingException("Ошибка обработки данных от ЦБР");
        }
    }
}

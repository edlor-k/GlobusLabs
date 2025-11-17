package ru.globus.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * Представление ответа от ЦБР
 */
@Getter
@Setter
@NoArgsConstructor
@JacksonXmlRootElement(localName = "ValCurs")
public class CbrResponse {

    /** Атрибут "Date" из XML, например: 02.03.2002 */
    @JacksonXmlProperty(isAttribute = true, localName = "Date")
    private String date;

    /** Атрибут "name" из XML */
    @JacksonXmlProperty(isAttribute = true, localName = "name")
    private String name;

    /** Список элементов <Valute> */
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Valute")
    private List<CbrCurrency> currencies;
}

package ru.globus.dto;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "ValCurs")
public class CbrResponse {

    /** Атрибут "Date" из XML, например: 02.03.2002 */
    @XmlAttribute(name = "Date")
    private String date;

    /** Атрибут "name" из XML (обычно "Foreign Currency Market") */
    @XmlAttribute(name = "name")
    private String name;

    /** Список элементов <Valute> */
    @XmlElement(name = "Valute")
    private List<CbrCurrency> currencies;
}

package ru.globus.dto;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
* Представление отдельной валюты в XML ответе от CBR
* */
@Getter
@Setter
@NoArgsConstructor
public class CbrCurrency {
    /** Атрибут ID валюты, например "R01235" */
    @XmlAttribute(name = "ID")
    private String id;

    /** Числовой код валюты (например 840 для USD) */
    @XmlElement(name = "NumCode")
    private String numCode;

    /** Буквенный код валюты, например "USD" */
    @XmlElement(name = "CharCode")
    private String code;

    /** Номинал (например 10 для "10 Датских крон") */
    @XmlElement(name = "Nominal")
    private int nominal;

    /** Русскоязычное название валюты */
    @XmlElement(name = "Name")
    private String name;

    /** Курс валюты (например "30,9436") */
    @XmlElement(name = "Value")
    private String value;

    /** Альтернативное поле (иногда дублирует Value) */
    @XmlElement(name = "VunitRate")
    private String vunitRate;
}

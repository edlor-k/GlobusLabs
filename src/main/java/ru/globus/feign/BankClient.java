package ru.globus.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.MediaType;

/**
 * Клиент для получения информации о курсах валют
 * с сайта Центрального Банка России (ЦБР).
 */
@FeignClient(
    name = "bankClient",
    url = "${bank.api.url}"
)
public interface BankClient {

    /**
     * Запрашивает XML с курсами валют на текущий день.
     *
     * @return XML-строка с данными о курсах валют
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_XML_VALUE)
    String getDailyRates();
}

package ru.globus.service.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Фейковый шедулер, имитирующий обновление курсов валют.
 *
 * @author Vladlen Korablev
 */
@Slf4j
@Service
@Profile("fake-scheduler")
public class FakeCurrencyRateUpdater implements CurrencyRateUpdater {

    @PostConstruct
    public void init() {
        log.info("FAKE: инициализация при запуске");
        updateRates();
    }

    @Override
    @Scheduled(cron = "0 0 3 * * *")
    public void updateRates() {
        log.info("FAKE: имитация обновления курсов валют");
    }
}

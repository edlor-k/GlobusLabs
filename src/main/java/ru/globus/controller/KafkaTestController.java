package ru.globus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.globus.dto.BankAccountEventDto;
import ru.globus.kafka.BankAccountEventProducer;
import ru.globus.model.enums.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Контроллер для тестирования отправки событий в Kafka.
 * Используется только в учебных целях.
 *
 * @author Vladlen Korablev
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/kafka")
@RequiredArgsConstructor
@Tag(name = "Kafka Test", description = "API для тестирования Kafka (учебный)")
public class KafkaTestController {

    private final BankAccountEventProducer eventProducer;

    /**
     * Отправить тестовое событие в Kafka.
     *
     * @param eventType тип события
     * @return результат отправки
     */
    @PostMapping("/send-test-event")
    @Operation(summary = "Отправить тестовое событие в Kafka",
            description = "Создаёт и отправляет тестовое событие в топик bank-account-events")
    public ResponseEntity<String> sendTestEvent(
            @RequestParam(defaultValue = "ACCOUNT_CREATED") BankAccountEventDto.EventType eventType) {

        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        BankAccountEventDto event = BankAccountEventDto.builder()
                .eventType(eventType)
                .accountId(accountId)
                .userId(userId)
                .balance(new BigDecimal("1000.00"))
                .currency(CurrencyCode.RUB)
                .timestamp(LocalDateTime.now())
                .message("Тестовое событие из KafkaTestController")
                .build();

        log.info("Отправка тестового события: eventType={}, accountId={}", eventType, accountId);
        eventProducer.sendEvent(event);

        return ResponseEntity.ok(
                String.format("Тестовое событие %s успешно отправлено для счёта %s", eventType, accountId)
        );
    }

    /**
     * Отправить пользовательское событие в Kafka.
     *
     * @param event событие для отправки
     * @return результат отправки
     */
    @PostMapping("/send-custom-event")
    @Operation(summary = "Отправить пользовательское событие",
            description = "Отправляет пользовательское событие в топик bank-account-events")
    public ResponseEntity<String> sendCustomEvent(@RequestBody BankAccountEventDto event) {
        log.info("Отправка пользовательского события: eventType={}, accountId={}",
                event.getEventType(), event.getAccountId());

        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }

        eventProducer.sendEvent(event);

        return ResponseEntity.ok(
                String.format("Событие %s успешно отправлено для счёта %s",
                        event.getEventType(), event.getAccountId())
        );
    }

    /**
     * Отправить массовые тестовые события.
     *
     * @param count количество событий
     * @return результат отправки
     */
    @PostMapping("/send-bulk-events")
    @Operation(summary = "Отправить массовые события",
            description = "Отправляет указанное количество тестовых событий для проверки производительности")
    public ResponseEntity<String> sendBulkEvents(@RequestParam(defaultValue = "10") int count) {
        log.info("Отправка {} тестовых событий", count);

        for (int i = 0; i < count; i++) {
            UUID accountId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            BankAccountEventDto event = BankAccountEventDto.builder()
                    .eventType(BankAccountEventDto.EventType.BALANCE_CHANGED)
                    .accountId(accountId)
                    .userId(userId)
                    .balance(new BigDecimal(String.format("%d.00", 1000 + i * 100)))
                    .currency(CurrencyCode.RUB)
                    .timestamp(LocalDateTime.now())
                    .message(String.format("Массовое событие #%d", i + 1))
                    .build();

            eventProducer.sendEvent(event);
        }

        return ResponseEntity.ok(String.format("Успешно отправлено %d событий", count));
    }
}

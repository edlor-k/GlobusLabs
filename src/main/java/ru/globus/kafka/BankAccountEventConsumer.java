package ru.globus.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.globus.dto.BankAccountEventDto;

/**
 * Consumer для обработки событий банковских счетов из Kafka.
 *
 * @author Vladlen Korablev
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BankAccountEventConsumer {

    /**
     * Слушает топик с событиями банковских счетов и обрабатывает их.
     *
     * @param event     событие банковского счета
     * @param partition партиция Kafka
     * @param offset    оффсет сообщения
     */
    @KafkaListener(
            topics = "${kafka.topics.bank-account-events}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(
            @Payload BankAccountEventDto event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("====================================");
        log.info("Получено событие из Kafka:");
        log.info("Partition: {}, Offset: {}", partition, offset);
        log.info("Event Type: {}", event.getEventType());
        log.info("Account ID: {}", event.getAccountId());
        log.info("User ID: {}", event.getUserId());
        log.info("Balance: {} {}", event.getBalance(), event.getCurrency());
        log.info("Timestamp: {}", event.getTimestamp());
        log.info("Message: {}", event.getMessage());
        log.info("====================================");

        processEvent(event);
    }

    /**
     * Обрабатывает полученное событие.
     *
     * @param event событие для обработки
     */
    private void processEvent(BankAccountEventDto event) {
        switch (event.getEventType()) {
            case ACCOUNT_CREATED -> handleAccountCreated(event);
            case ACCOUNT_UPDATED -> handleAccountUpdated(event);
            case ACCOUNT_DELETED -> handleAccountDeleted(event);
            case BALANCE_CHANGED -> handleBalanceChanged(event);
            case TRANSFER_COMPLETED -> handleTransferCompleted(event);
            default -> log.warn("Неизвестный тип события: {}", event.getEventType());
        }
    }

    private void handleAccountCreated(BankAccountEventDto event) {
        log.info("Обработка создания счета: accountId={}, userId={}",
                event.getAccountId(), event.getUserId());
        // потенциально здесь может быть логика: отправка уведомления, запись в аудит, и т.д.
    }

    private void handleAccountUpdated(BankAccountEventDto event) {
        log.info("Обработка обновления счета: accountId={}", event.getAccountId());
        // потенциально логика обработки обновления
    }

    private void handleAccountDeleted(BankAccountEventDto event) {
        log.info("Обработка удаления счета: accountId={}", event.getAccountId());
        // потенциально логика обработки удаления
    }

    private void handleBalanceChanged(BankAccountEventDto event) {
        log.info("Обработка изменения баланса: accountId={}, newBalance={}",
                event.getAccountId(), event.getBalance());
        // потенциально логика обработки изменения баланса
    }

    private void handleTransferCompleted(BankAccountEventDto event) {
        log.info("Обработка завершения перевода: accountId={}", event.getAccountId());
        // потенциально логика обработки перевода
    }
}

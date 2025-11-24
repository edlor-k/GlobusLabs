package ru.globus.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import ru.globus.dto.BankAccountEventDto;

import java.util.concurrent.CompletableFuture;

/**
 * Producer для отправки событий банковских счетов в Kafka.
 *
 * @author Vladlen Korablev
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BankAccountEventProducer {

    private final KafkaTemplate<String, BankAccountEventDto> kafkaTemplate;

    @Value("${kafka.topics.bank-account-events}")
    private String topic;

    /**
     * Отправляет событие банковского счета в Kafka.
     *
     * @param event событие для отправки
     */
    public void sendEvent(BankAccountEventDto event) {
        log.info("Отправка события в Kafka: topic={}, eventType={}, accountId={}",
                topic, event.getEventType(), event.getAccountId());

        CompletableFuture<SendResult<String, BankAccountEventDto>> future =
                kafkaTemplate.send(topic, event.getAccountId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Событие успешно отправлено: topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Ошибка при отправке события в Kafka: {}", ex.getMessage(), ex);
            }
        });
    }
}

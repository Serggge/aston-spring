package ru.serggge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.serggge.config.properties.KafkaProducerProperties;
import ru.serggge.entity.OutboxEvent;
import ru.serggge.model.AccountEvent;
import ru.serggge.model.PojoAccountEvent;
import ru.serggge.repository.OutboxRepository;
import ru.serggge.service.client.NotificationClient;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxServiceImpl implements OutboxService {

    private final KafkaTemplate<String, AccountEvent> kafkaTemplate;
    private final OutboxRepository outboxRepository;
    private final NotificationClient notificationClient;
    private final KafkaProducerProperties kafkaProperties;

    @Override
    @Scheduled(fixedRateString = "${configuration.scheduler.delay}")
    public void eventProcessing() {
        outboxRepository.findAllNonBlocked()
                        .forEach(this::sendEvent);
    }

    private void sendEvent(OutboxEvent outboxEvent) {
        if (kafkaProperties.isEnabled()) {
            sendUsingKafka(outboxEvent);
        } else {
            sendUsingRest(outboxEvent);
        }
        outboxRepository.deleteById(outboxEvent.getId());
    }

    private void sendUsingKafka(OutboxEvent outboxEvent) {
        AccountEvent accountEvent = new AccountEvent(
                outboxEvent.getEmail(),
                outboxEvent.getEvent().name(),
                Instant.now());

        kafkaTemplate.send(kafkaProperties.getTopicName(), accountEvent.getEmail(), accountEvent)
                     .whenComplete((sendResult, exception) -> {
                         if (exception == null) {
                             RecordMetadata record = sendResult.getRecordMetadata();
                             printLogSuccess(accountEvent, record);
                         } else {
                             printLogFail(accountEvent, exception);
                             sendUsingRest(outboxEvent);
                         }
                     });
    }

    private void sendUsingRest(OutboxEvent outboxEvent) {
        PojoAccountEvent accountEvent = new PojoAccountEvent(outboxEvent.getEmail(), outboxEvent.getEvent().name());
        notificationClient.sendNotification(accountEvent);
    }

    private void printLogSuccess(AccountEvent event, RecordMetadata record) {
        log.info("Event: {} successfully sent to topic: [{}] partition: [{}] at [{}]",
                event, record.topic(), record.partition(), Instant.ofEpochMilli(record.timestamp()));
    }

    private void printLogFail(AccountEvent event, Throwable exception) {
        log.warn("Can't send the event: {} to broker by reason: {}. Event will send by REST",
                event, exception.getMessage());
    }

}
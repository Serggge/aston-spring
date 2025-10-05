package ru.serggge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.serggge.config.properties.KafkaProducerProperties;
import ru.serggge.entity.OutboxEvent;
import ru.serggge.model.AccountEvent;
import ru.serggge.repository.OutboxRepository;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxServiceImpl implements OutboxService {

    private final KafkaTemplate<String, AccountEvent> kafkaTemplate;
    private final OutboxRepository outboxRepository;
    private final KafkaProducerProperties kafkaProperties;

    @Override
    @Scheduled(fixedRateString = "${kafka.configuration.scheduled}")
    @Transactional
    public void eventProcessing() {
        outboxRepository.findUnsentEvents()
                        .stream()
                        .map(this::mapToAccountEvent)
                        .forEach(this::sendEvent);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void sendEvent(Map.Entry<Long, AccountEvent> entry) {
        kafkaTemplate.send(kafkaProperties.getTopicName(),
                             entry.getValue().email(),
                             entry.getValue())
                     .thenAccept(sendResult -> {
                         RecordMetadata record = sendResult.getRecordMetadata();
                         printLog(entry.getValue(), record);
                     });
        outboxRepository.markAsCompleted(entry.getKey());
    }

    private Map.Entry<Long, AccountEvent> mapToAccountEvent(OutboxEvent outboxEvent) {
        AccountEvent accountEvent = new AccountEvent(
                outboxEvent.getEmail(),
                outboxEvent.getEvent().name(),
                outboxEvent.getCreatedAt().toEpochMilli());
        return Map.entry(outboxEvent.getId(), accountEvent);
    }

    private void printLog(AccountEvent event, RecordMetadata record) {
        log.info("Event: {} successfully sent to topic: [{}] partition: [{}] at [{}]",
                event, record.topic(), record.partition(), Instant.ofEpochMilli(record.timestamp()));
    }
}
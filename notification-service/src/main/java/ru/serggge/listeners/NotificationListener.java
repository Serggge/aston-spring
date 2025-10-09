package ru.serggge.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.retry.annotation.Backoff;
import ru.serggge.model.AccountEvent;
import ru.serggge.model.EmailMessage;
import ru.serggge.model.Event;
import ru.serggge.service.MailService;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final MailService mailService;

    @RetryableTopic(
            backoff = @Backoff(delay = 1000, multiplier = 2),
            attempts = "3",
            autoCreateTopics = "false",
            exclude = NullPointerException.class)
    @KafkaListener(topics = "${kafka.configuration.topics}", groupId = "${kafka.configuration.group-id}")
    public void handle(ConsumerRecord<String, AccountEvent> record) {
        log.info("Message received: {}, from topic: {}, partition: {}", record, record.topic(), record.partition());
        AccountEvent event = record.value();
        EmailMessage message = new EmailMessage(
                event.getEmail(),
                Event.valueOf(event.getEvent()),
                event.getCreatedAt());
        mailService.saveMessage(message);
    }

    @DltHandler
    public void processDltMessage(ConsumerRecord<String, AccountEvent> record) {
        log.warn("Can't process the record: {}", record);
    }
}
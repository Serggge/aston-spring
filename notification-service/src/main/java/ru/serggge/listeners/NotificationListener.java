package ru.serggge.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.*;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.stereotype.Component;
import org.springframework.retry.annotation.Backoff;
import ru.serggge.model.AccountEvent;
import ru.serggge.model.EmailMessage;
import ru.serggge.model.Event;
import ru.serggge.service.MailService;
import java.net.SocketTimeoutException;
import java.time.Instant;

@Component
@KafkaListener(topics = "${kafka.configuration.topics}", groupId = "${kafka.configuration.group-id}")
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final MailService mailService;

    @KafkaHandler
    @RetryableTopic(
            backoff = @Backoff(delay = 1000L, multiplier = 2, maxDelay = 5000L),
            attempts = "3",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR,
            include = SocketTimeoutException.class, exclude = NullPointerException.class)
    public void handle(ConsumerRecord<String, AccountEvent> record) {
        log.info("Message received: {}, from topic: {}, partition: {}", record, record.topic(), record.partition());
        AccountEvent event = record.value();
        EmailMessage message = new EmailMessage(
                event.email(),
                Event.valueOf(event.event()),
                Instant.ofEpochMilli(event.createdAt()));
        mailService.saveMessage(message);
    }

    @DltHandler
    public void processDltMessage(ConsumerRecord<String, AccountEvent> record) {
        log.warn("Can't process the record: {}", record);
    }
}
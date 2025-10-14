package ru.serggge;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.serggge.config.NotificationServiceTestConfig;
import ru.serggge.entity.Mail;
import ru.serggge.model.AccountEvent;
import ru.serggge.model.Event;
import ru.serggge.repository.MailRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("test")
@Import(NotificationServiceTestConfig.class)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaNotificationServiceTest {

    @Autowired
    KafkaTemplate<String, AccountEvent> kafkaTemplate;
    @Autowired
    MailRepository mailRepository;
    @Value("${kafka.configuration.topics}")
    String topicName;

    @AfterEach
    void cleanUp() {
        mailRepository.deleteAll();
    }

    @Test
    @DisplayName("Receive Kafka message")
    void messageIsReceivedAndSaved() {
        // given
        final String email = "john@email";
        final String event = Event.CREATE.name();
        final Instant createdAt = Instant.now();
        final AccountEvent accountEvent = new AccountEvent(email, event, createdAt);

        // when
        kafkaTemplate.send(topicName, email, accountEvent);

        // then
        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> {

                    Optional<Mail> mail = mailRepository.findByEmail(email);

                    assertThat(mail.isPresent(), is(true));
                    assertThat(mail.get().getEmail(), is(email));
                });
    }

}
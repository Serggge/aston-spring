package ru.serggge;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import ru.serggge.config.NotificationTestConfig;
import ru.serggge.model.AccountEvent;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(NotificationTestConfig.class)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaNotificationServiceTest {

    @Autowired
    static KafkaContainer kafkaContainer;
    @Autowired
    static RedisContainer redisContainer;
    @Autowired
    KafkaTemplate<String, AccountEvent> kafkaTemplate;

    @Test
    void loadContext() {

    }
}
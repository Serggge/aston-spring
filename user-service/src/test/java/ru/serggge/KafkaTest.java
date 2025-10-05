package ru.serggge;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(topics = "email-notification", bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class KafkaTest {
}

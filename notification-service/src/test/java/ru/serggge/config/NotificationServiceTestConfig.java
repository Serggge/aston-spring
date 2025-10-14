package ru.serggge.config;

import com.redis.testcontainers.RedisContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration(proxyBeanMethods = false)
public class NotificationServiceTestConfig {

    @Value("${images.kafka}")
    private String kafkaDockerImage;
    @Value("${images.redis}")
    private String redisDockerImage;

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse(kafkaDockerImage))
                .withReuse(true);
    }

    @Bean
    @ServiceConnection
    public RedisContainer redisContainer() {
        return new RedisContainer(DockerImageName.parse(redisDockerImage))
                .withReuse(true);
    }

}
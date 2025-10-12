package ru.serggge.config;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.serggge.config.properties.KafkaProducerProperties;
import ru.serggge.model.AccountEvent;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class KafkaProducerConfig {

    private final KafkaProducerProperties producerProps;

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> {
            log.info("BOOSTRAP SERVERS: {}", producerProps.getBootstrapServers());
        };
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(producerProps.getTopicName())
                           .partitions(producerProps.getPartitions())
                           .replicas(producerProps.getPartitions())
                           .build();
    }

    @Bean
    public KafkaTemplate<String, AccountEvent> kafkaTemplate(ProducerFactory<String, AccountEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, AccountEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(senderProps());
    }

    private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProps.getBootstrapServers());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "user-service");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, producerProps.getSrUrl());
        return props;
    }
}

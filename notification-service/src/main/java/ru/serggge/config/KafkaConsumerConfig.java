package ru.serggge.config;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import ru.serggge.model.AccountEvent;
import ru.serggge.properties.KafkaConsumerProperties;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerConfig {

    private final KafkaConsumerProperties kafkaProperties;

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> {
            log.info("BOOSTRAP SERVERS: {}", kafkaProperties.getBootstrapServers());
        };
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, AccountEvent>
    kafkaListenerContainerFactory(ConsumerFactory<String, AccountEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, AccountEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> adminProps = new HashMap<>();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        adminProps.put(AdminClientConfig.CLIENT_ID_CONFIG, "consumer-admin-client");
        return new KafkaAdmin(adminProps);
    }

    @Bean
    public ConsumerFactory<String, AccountEvent> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaProperties.getClientId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaProperties.getSrUrl());
        props.put(AbstractKafkaSchemaSerDeConfig.NORMALIZE_SCHEMAS, true);
        return props;
    }

}

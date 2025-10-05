package ru.serggge.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "kafka.configuration")
@Component
@Data
public class KafkaConsumerProperties {

    private List<String> bootstrapServers;
    private String groupId;
    private String clientId;

}

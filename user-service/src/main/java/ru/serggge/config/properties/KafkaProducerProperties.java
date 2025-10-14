package ru.serggge.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@ConfigurationProperties(prefix = "kafka.configuration")
@Component
@Data
public class KafkaProducerProperties {

    private List<String> bootstrapServers;
    private String topicName;
    private int partitions;
    private String srUrl;
}

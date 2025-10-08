package ru.serggge.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scheduler")
@Data
public class SchedulerProperties {

    private boolean enabled;
    private int batchSize;
    private String delay;
    private String lockAtLeastFor;
    private String lockAtMostFor;
}
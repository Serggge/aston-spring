package ru.serggge.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scheduling.configuration")
@Data
public class SchedulerProperties {

    private boolean enabled;
    private long delay;
}

package ru.serggge.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "configuration.swagger")
@ConfigurationPropertiesScan
@Component
@Data
public class SwaggerProperties {

    private String appName;
    private String appDescription;
    private String appVersion;
}

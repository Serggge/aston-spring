package ru.serggge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "swagger")
@Data
public class SwaggerProperties {

    private String appName;
    private String appDescription;
    private String appVersion;
}

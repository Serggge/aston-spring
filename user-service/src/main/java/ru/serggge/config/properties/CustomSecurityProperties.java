package ru.serggge.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "resource-server")
@Data
public class CustomSecurityProperties {

    private String jwkSetUri;
}

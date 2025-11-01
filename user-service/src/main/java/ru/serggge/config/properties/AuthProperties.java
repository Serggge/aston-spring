package ru.serggge.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "configuration.auth")
@Data
public class AuthProperties {

    private String authServerUrl;
    private String jwkSetUri;
    private String tokenUri;
}

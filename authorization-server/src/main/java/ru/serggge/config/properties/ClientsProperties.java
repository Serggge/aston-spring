package ru.serggge.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "configuration.clients")
@RefreshScope
@Data
public class ClientsProperties {

    private String gatewaySecret;
    private String gatewayRedirectUri;
    private String userServiceSecret;
    private String mailServiceSecret;
}

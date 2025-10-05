package ru.serggge.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.mail")
@Component
@Data
public class MailProperties {

    private String host;
    private String port;
    private String username;
    private String password;
}

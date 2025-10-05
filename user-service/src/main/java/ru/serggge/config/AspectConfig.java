package ru.serggge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.serggge.aspect.NotificationAspect;

@Configuration
@EnableAspectJAutoProxy
public class AspectConfig {

    @Bean
    public NotificationAspect notificationAspect() {
        return new NotificationAspect();
    }
}
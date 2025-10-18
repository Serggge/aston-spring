package ru.serggge.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.serggge.aspect.MicrometerAspect;
import ru.serggge.aspect.NotificationAspect;

@Configuration
@EnableAspectJAutoProxy
public class AspectConfig {

    @Bean
    public NotificationAspect notificationAspect() {
        return new NotificationAspect();
    }

    @Bean
    public MicrometerAspect micrometerAspect(MeterRegistry meterRegistry) {
        return new MicrometerAspect(meterRegistry);
    }

    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }
}
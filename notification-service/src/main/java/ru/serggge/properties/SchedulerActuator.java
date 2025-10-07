package ru.serggge.properties;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "scheduler")
@RequiredArgsConstructor
public class SchedulerActuator {

    private final SchedulerProperties schedulerProperties;

    @WriteOperation
    public void changeConfiguration(String param, String value) {
        switch (param) {
            case "enabled":
                schedulerProperties.setEnabled(Boolean.parseBoolean(value));
            case "delay":
                schedulerProperties.setDelay(Long.parseLong(value));
            default:
                throw new UnsupportedOperationException("Bad request parameters");
        }
    }

}
package ru.serggge.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.serggge.dto.NotificationResponse;
import ru.serggge.model.PojoAccountEvent;

@FeignClient(name = "notification-service",
        url = "${configuration.services.mail-service-uri}")
public interface NotificationClient {

    @PostMapping("/mail")
    NotificationResponse sendNotification(@RequestBody PojoAccountEvent accountEvent);

}
package ru.serggge.service.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.serggge.dto.NotificationResponse;
import ru.serggge.model.AccountEvent;

@FeignClient("notification-service")
public interface NotificationProxy {

    @PostMapping("/mail")
    NotificationResponse sendNotification(@RequestBody AccountEvent accountEvent);

}
package ru.serggge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.serggge.dto.NotificationResponse;
import ru.serggge.model.AccountEvent;
import ru.serggge.service.proxy.NotificationProxy;

@Service
@RequiredArgsConstructor
public class RestSenderServiceImpl implements RestSenderService {

    private final NotificationProxy proxy;

    @Override
    public NotificationResponse sendEvent(AccountEvent event) {
        return proxy.sendNotification(event);
    }
}

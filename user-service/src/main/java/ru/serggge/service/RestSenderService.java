package ru.serggge.service;

import ru.serggge.dto.NotificationResponse;
import ru.serggge.model.AccountEvent;

public interface RestSenderService {

    NotificationResponse sendEvent(AccountEvent event);
}

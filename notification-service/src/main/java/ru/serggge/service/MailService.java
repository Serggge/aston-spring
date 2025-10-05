package ru.serggge.service;

import ru.serggge.model.EmailMessage;

public interface MailService {

    void send(EmailMessage message);
}

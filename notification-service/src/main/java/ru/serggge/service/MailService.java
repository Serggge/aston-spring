package ru.serggge.service;

import ru.serggge.entity.Mail;
import ru.serggge.model.EmailMessage;

public interface MailService {

    Mail saveMessage(EmailMessage message);

}

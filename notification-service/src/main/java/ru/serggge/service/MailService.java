package ru.serggge.service;

import ru.serggge.entity.Mail;
import ru.serggge.model.EmailMessage;
import java.util.List;

public interface MailService {

    Mail saveMessage(EmailMessage message);

    List<Mail> getAll(String mail, int page, int pageSize);
}

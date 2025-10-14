package ru.serggge.service;

import ru.serggge.entity.Mail;

import java.util.List;

public interface MailSenderService {

    void send(Mail mail);

}
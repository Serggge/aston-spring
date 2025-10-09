package ru.serggge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.serggge.entity.Mail;
import ru.serggge.model.EmailMessage;
import ru.serggge.model.Event;
import ru.serggge.repository.MailRepository;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final MailRepository mailRepository;

    @Override
    @Transactional
    public Mail saveMessage(EmailMessage message) {
        String messageText = prepareMessageTemplate(message.event());
        Mail mail = new Mail(message.email(), messageText, message.createdAt());
        return mailRepository.save(mail);
    }

    private String prepareMessageTemplate(Event event) {
        return switch (event) {
            case CREATE -> "Здравствуйте! Ваш аккаунт на сайте <Aston> был успешно создан.";
            case UPDATE -> "Здравствуйте! Ваш аккаунт на сайте <Aston> был обновлён.";
            case DELETE -> "Здравствуйте! Ваш аккаунт был удалён.";
        };
    }
}

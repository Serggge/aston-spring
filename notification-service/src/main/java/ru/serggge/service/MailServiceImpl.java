package ru.serggge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.serggge.model.EmailMessage;
import ru.serggge.model.Event;
import ru.serggge.properties.MailProperties;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private static final String SUBJECT = "Информация о действии вашего аккаунта на сайте Aston";
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    public void send(EmailMessage message) {
        SimpleMailMessage messageToSend = new SimpleMailMessage();
        messageToSend.setFrom(mailProperties.getUsername());
        messageToSend.setTo(message.email());
        messageToSend.setSubject(SUBJECT);
        String messageBody = prepareMessageBody(message.event());
        messageToSend.setText(messageBody);
//        mailSender.send(messageToSend);
        log.info("Email sent successfully to {} at {}", message.email(), LocalDateTime.now());
    }

    private String prepareMessageBody(Event event) {
        return switch (event) {
            case CREATE -> "Здравствуйте!\nВаш аккаунт на сайте <Aston> был успешно создан.";
            case UPDATE -> "Здравствуйте!\nВаш аккаунт на сайте <Aston> был обновлён.";
            case DELETE -> "Здравствуйте!\nВаш аккаунт был удалён.";
        };
    }
}

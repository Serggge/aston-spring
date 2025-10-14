package ru.serggge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.serggge.entity.Mail;
import ru.serggge.properties.MailSenderProperties;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailSenderServiceImpl implements MailSenderService {

    private static final String SUBJECT = "Информация о действии вашего аккаунта на сайте Aston";
    private final JavaMailSender mailSender;
    private final MailSenderProperties senderProps;

    @Override
    public void send(Mail mail) {
        SimpleMailMessage messageToSend = new SimpleMailMessage();
        messageToSend.setFrom(senderProps.getUsername());
        messageToSend.setTo(mail.getEmail());
        messageToSend.setSubject(SUBJECT);
        String messageBody = (mail.getMessage());
        messageToSend.setText(messageBody);
//        mailSender.send(messageToSend);
        log.info("Email sent successfully to {} at {}", mail.getEmail(), LocalDateTime.now());
    }

}
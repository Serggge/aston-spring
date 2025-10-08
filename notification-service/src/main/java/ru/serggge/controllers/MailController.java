package ru.serggge.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.serggge.dto.SendMailRequestDto;
import ru.serggge.entity.Mail;
import ru.serggge.mappers.MailMapper;
import ru.serggge.model.EmailMessage;
import ru.serggge.service.MailService;

@RestController
@RequiredArgsConstructor
public class MailController implements MailOperations {

    private final MailService mailService;
    private final MailMapper mailMapper;

    @Override
    public Mail sendMail(SendMailRequestDto requestDto) {
        EmailMessage emailMessage = mailMapper.mapToEmailMessage(requestDto);
        return mailService.saveMessage(emailMessage);
    }

}
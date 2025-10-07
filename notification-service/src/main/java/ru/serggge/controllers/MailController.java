package ru.serggge.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.serggge.dto.FindEmailResponseDto;
import ru.serggge.dto.SendMailRequestDto;
import ru.serggge.entity.Mail;
import ru.serggge.mappers.MailMapper;
import ru.serggge.model.EmailMessage;
import ru.serggge.service.MailSenderService;
import ru.serggge.service.MailService;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MailController implements MailOperations {

    private final MailService mailService;
    private final MailSenderService mailSenderService;
    private final MailMapper mailMapper;

    @Override
    public void sendMail(SendMailRequestDto requestDto) {
        EmailMessage emailMessage = mailMapper.mapToEmailMessage(requestDto);
        mailService.saveMessage(emailMessage);
    }

    @Override
    public List<FindEmailResponseDto> getMails(String email, int page, int pageSize) {
        List<Mail> mails = mailService.getAll(email, page, pageSize);
        return mailMapper.mapToResponseDto(mails);
    }
}
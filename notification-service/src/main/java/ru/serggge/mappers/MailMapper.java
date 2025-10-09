package ru.serggge.mappers;

import org.springframework.stereotype.Component;
import ru.serggge.dto.SendMailResponseDto;
import ru.serggge.dto.SendMailRequestDto;
import ru.serggge.entity.Mail;
import ru.serggge.model.EmailMessage;
import ru.serggge.model.Event;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

@Component
public class MailMapper {

    public SendMailResponseDto mapToResponseDto(Mail mail) {
        return new SendMailResponseDto(
                mail.getId().toString(),
                mail.getEmail(),
                mail.getMessage(),
                LocalDateTime.ofInstant(mail.getReceivedAt(), ZoneId.systemDefault()));
    }

    public List<SendMailResponseDto> mapToResponseDto(Collection<Mail> mails) {
        return mails.stream()
                    .map(this::mapToResponseDto)
                    .toList();
    }

    public EmailMessage mapToEmailMessage(SendMailRequestDto requestDto) {
        return new EmailMessage(
                requestDto.getEmail(),
                Event.valueOf(requestDto.getEvent()
                                        .toUpperCase()),
                Instant.now());
    }
}

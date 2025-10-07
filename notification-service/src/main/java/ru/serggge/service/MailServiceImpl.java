package ru.serggge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.serggge.entity.Mail;
import ru.serggge.model.EmailMessage;
import ru.serggge.model.Event;
import ru.serggge.repository.MailRepository;
import java.util.Collections;
import java.util.List;

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

    @Override
    @Transactional
    public List<Mail> getAll(String mail, int page, int pageSize) {
        Sort sorting = Sort.by(Sort.Direction.ASC, "receivedAt");
        Pageable pageRequest = PageRequest.of(page, pageSize, sorting);
        Slice<Mail> mails = mailRepository.findByMail(mail.toLowerCase(), pageRequest);
        if (mails.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<Long> ids = mails.stream()
                                  .map(Mail::getId)
                                  .toList();
            mailRepository.markAsDelivered(ids);
            return mails.getContent();
        }
    }

    private String prepareMessageTemplate(Event event) {
        return switch (event) {
            case CREATE -> "Здравствуйте!\nВаш аккаунт на сайте <Aston> был успешно создан.";
            case UPDATE -> "Здравствуйте!\nВаш аккаунт на сайте <Aston> был обновлён.";
            case DELETE -> "Здравствуйте!\nВаш аккаунт был удалён.";
        };
    }
}

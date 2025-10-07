package ru.serggge.service;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.serggge.repository.MailRepository;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InboxServiceImpl implements InboxService {

    private final MailRepository mailRepository;
    private final MailSenderService mailSenderService;

    @Override
    @Scheduled(fixedDelayString = "${scheduler.delay}")
    @SchedulerLock(name = "MailSender", lockAtLeastFor = "PT1M", lockAtMostFor = "PT2M")
    public void eventProcessing() {
        List<Long> sentMailIds = new LinkedList<>();
        try {
            mailRepository.findUnsent()
                          .forEach(mail -> {
                              mailSenderService.send(mail);
                              sentMailIds.add(mail.getId());
                          });
        } finally {
            mailRepository.markAsDelivered(sentMailIds);
        }
    }
}
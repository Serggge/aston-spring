package ru.serggge.service;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.serggge.properties.SchedulerProperties;
import ru.serggge.repository.MailRepository;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InboxServiceImpl implements InboxService {

    private final MailRepository mailRepository;
    private final MailSenderService mailSenderService;
    private final SchedulerProperties schedulerProps;

    @Override
    @Scheduled(initialDelay = 2000, fixedDelayString = "${scheduler.delay}")
    @SchedulerLock(name = "MailSender",
            lockAtLeastFor = "${scheduler.lockAtLeastFor}",
            lockAtMostFor = "${scheduler.lockAtMostFor}")
    public void eventProcessing() {
        if (schedulerProps.isEnabled()) {
            Pageable batch = PageRequest.of(0, schedulerProps.getBatchSize());
            List<UUID> sentMailIds = new LinkedList<>();
            try {
                mailRepository.findAll(batch)
                              .forEach(mail -> {
                                  mailSenderService.send(mail);
                                  sentMailIds.add(mail.getId());
                              });
            } finally {
                mailRepository.deleteAllById(sentMailIds);
            }
        }
    }
}
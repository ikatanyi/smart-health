package io.smarthealth.messaging;

import io.smarthealth.infrastructure.mail.EmailData;
import io.smarthealth.infrastructure.mail.MailService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MailService mailService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    //update the queue and we can go ahead and attach the user 

    @Scheduled(cron = "*/5 * * * * *")
    public void performTask() {
        Instant now = Instant.now();
        this.simpMessagingTemplate.convertAndSend("/queue/now", now);
    }

    @Async
    public void sendEmailNotification(EmailData data) {
        try {
            mailService.send(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

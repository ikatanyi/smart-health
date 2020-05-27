package io.smarthealth.messaging;

import io.smarthealth.infrastructure.mail.EmailData;
import io.smarthealth.infrastructure.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MailService mailService;

    @Async
    public void sendEmailNotification(EmailData data) {
        try {
            mailService.send(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

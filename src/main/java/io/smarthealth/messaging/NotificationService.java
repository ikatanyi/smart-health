package io.smarthealth.messaging;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
      private final SimpMessagingTemplate simpMessagingTemplate;
      //update the queue and we can go ahead and attach the user 
      @Scheduled(cron = "*/5 * * * * *")
    public void performTask() {
        Instant now = Instant.now();
        this.simpMessagingTemplate.convertAndSend("/queue/now", now);
    }
      
}

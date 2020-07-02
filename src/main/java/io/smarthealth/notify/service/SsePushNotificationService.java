package io.smarthealth.notify.service;

import io.smarthealth.events.data.PatientResultEvent;
import io.smarthealth.notifications.domain.Notification;
import io.smarthealth.notifications.domain.NotificationType;
import io.smarthealth.notifications.service.NotificationService;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.domain.UserRepository;
import io.smarthealth.security.service.UserService;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 *
 * @author Kelsas
 */
@Service
@EnableScheduling
public class SsePushNotificationService {

    private final NotificationService notificationService;

    final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
    final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SsePushNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void addEmitter(final SseEmitter emitter) {
        emitters.add(emitter);
    }

    public void removeEmitter(final SseEmitter emitter) {
        emitters.remove(emitter);
    }

    @Async
    @Scheduled(fixedRate = 5000)
    public void doNotify() throws IOException {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .data(DATE_FORMATTER.format(new Date()) + " : " + UUID.randomUUID().toString()));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }

    @EventListener
    protected void onNotification(PatientResultEvent event) {

        notificationService.notifyUser(event);

        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(event);
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }
//    @EventListener
//    public void onNotification(Notification notification) {
//        List<SseEmitter> deadEmitters = new ArrayList<>();
//        this.emitters.forEach(emitter -> {
//            try {
//
//                emitter.send(notification);
//            } catch (IOException e) {
//                deadEmitters.add(emitter);
//            }
//        });
//        this.emitters.remove(deadEmitters);
//    }
}

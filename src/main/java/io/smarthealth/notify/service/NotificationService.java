package io.smarthealth.notify.service;

import io.smarthealth.clinical.record.service.DoctorRequestService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.notify.data.NoticeType;
import io.smarthealth.notify.data.NotificationData;
import io.smarthealth.notify.events.DocRequestEvent;
import io.smarthealth.notify.domain.Notification;
import io.smarthealth.notify.domain.NotificationRepository;
import io.smarthealth.notify.domain.specification.NotificationSpecification;
import io.smarthealth.notify.events.UserNotificationEvent;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationEventPublisher notificationEventPublisher;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final DoctorRequestService requestService;
    private final UserService userService;

    @Transactional
    public Notification createNotification(NotificationData request) {
        User user = userService.findUserByUsernameOrEmail(request.getUsername()).orElse(null);
        Notification notify = new Notification(user, request.getDescription(), request.getNoticeType(), request.getReference());
        return notificationRepository.save(notify);
    }

    public Optional<Notification> getNotification(Long id) {
        return notificationRepository.findById(id);
    }

    @Transactional
    public void updateRead(Long id) {
        Notification notice = getNotification(id).orElse(null);
        if (notice != null) {
            notice.setRead(true);
            notificationRepository.save(notice);
        }
    }

    @Transactional
    public void updateReadAll(String username) {
        User user = userService.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.notFound("Username {0} does not exist", username));

        notificationRepository.readAllNotifications(user.getId());
    }

    public Page<Notification> getAllNotifications(String username, Boolean isRead, NoticeType noticeType, DateRange range, Pageable pageable) {
        Specification<Notification> spec = NotificationSpecification.createSpecification(username, isRead, noticeType, range);
        return notificationRepository.findAll(spec, pageable);
    }

    @Transactional
    public void deleteNotification(Long id) {
        Notification notice = getNotification(id).orElse(null);
        if (notice != null) {
            notificationRepository.delete(notice);
        }
    }

    /**
     * Send notification to users subscribed on channel "/user/queue/notify".
     *
     * The message will be sent only to the user with the given username.
     *
     * @param notification The notification message.
     * @param username The username for the user to send notification.
     */
    public void notify(Notification notification, String username) {
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notify",
                notification
        );
    }

//    @Async
//    @EventListener
    @TransactionalEventListener
    public void handleCreatedUpdatedEvent(DocRequestEvent e) {
        log.info("Doctor Requests notification ... ");
        e.getRequestType().forEach((type) -> {
            messagingTemplate.convertAndSend("/topic/requests." + type.name(), requestService.getUnfilledDoctorRequests(type));
        });
    }
 
    @Async
    @EventListener
//    @TransactionalEventListener
    public void notifyUser(UserNotificationEvent e) {

        if (e.getNotification() == null) {
            return;
        }

        NotificationData data = e.getNotification();

        Optional<User> user = userService.findUserByUsernameOrEmail(data.getUsername());
        if (user.isPresent()) {
            User toNotify = user.get();
            Notification notice = new Notification(toNotify, data.getDescription(), data.getNoticeType(), data.getReference());
            notice.setRead(false);
            //save
            Notification saveNotice = notificationRepository.save(notice); 
            this.messagingTemplate.convertAndSendToUser(toNotify.getUsername(), "/queue/notify", saveNotice.toData());

            System.err.println("Notifying user ... " + toNotify.getUsername() + " Message: " + saveNotice.getMessage());
        }
    }

    public void notifyUser(NotificationData data) {
        notificationEventPublisher.publishUserNotificationEvent(data);
    } 
}

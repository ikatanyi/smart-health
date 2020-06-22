package io.smarthealth.notifications.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.notifications.data.NotificationBlock;
import io.smarthealth.notifications.data.NotificationData;
import io.smarthealth.notifications.domain.Notification;
import io.smarthealth.notifications.domain.NotificationRepository;
import io.smarthealth.notifications.domain.NotificationType;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 *
 * @author Kelsas
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationService(NotificationRepository notificationRepository, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }
    

    @Transactional
    public void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    @Transactional
    public void deleteNotification(long id) {
        notificationRepository.deleteById(id);
    }

    public Page<NotificationData> getCurrentUserNotifications(String username, Pageable pageable) {
        User user = userService.findUserByUsernameOrEmail(username).orElseThrow(() -> APIException.notFound("User {0} Not Found", username));

        // Get a page of notifications using the pageable and current user as the notification recipient
        Page<NotificationData> notifications = notificationRepository.findByRecipient(user, pageable)
                .map(x -> toNotificationData(x));
        return notifications;
    }

    private NotificationData toNotificationData(Notification notification) {
        NotificationData data = new NotificationData();
        data.setNotificationId(notification.getId());
        data.setNotificationType(notification.getType());
        data.setRecipientId(notification.getRecipient().getId());
        data.setRecipientName(notification.getRecipient().getName());
        data.setSenderId(notification.getSender().getId());
        data.setSenderName(notification.getSender().getName());
        //TODO based on the notification have this list pull the exact message from the backed

        return data;
    }

    public void notifyUser(NotificationBlock notificationBlock) {
        User recipient = userService.findUserById(notificationBlock.getRecipient());
        User sender = userService.findUserById(notificationBlock.getSender());
        Notification notification = new Notification();
        notification.setReference(notificationBlock.getMessage());
        notification.setRecipient(recipient);
        notification.setSender(sender);
        notification.setType(NotificationType.DoctorResults);
        saveNotification(notification);
        
    }
 
}

package io.smarthealth.notifications.service;

import io.smarthealth.events.data.PatientResultEvent;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.notifications.data.NotificationBlock;
import io.smarthealth.notifications.data.NotificationData;
import io.smarthealth.notifications.domain.Notification;
import io.smarthealth.notifications.domain.NotificationRepository;
import io.smarthealth.notifications.domain.NotificationType;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void notifyUser(PatientResultEvent event) {
        String username = event.getUserID();
        Optional<User> user = userService.findUserByUsernameOrEmail(username);

        if (user.isPresent()) {
            Notification notification = new Notification();
            notification.setType(NotificationType.DoctorResults);
            notification.setRead(false);
            notification.setRecipient(user.get());
            notification.setReference(event.getPatientNo() + " - " + event.getPatientName() + " Results Have been Entered");
            saveNotification(notification);
        }

    }

}

package io.smarthealth.notify.events;

import io.smarthealth.notify.data.NotificationData;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author Kelsas
 */
public class UserNotificationEvent extends ApplicationEvent {

    private final String username;
    private final NotificationData notification;

    public UserNotificationEvent(Object source, String username, NotificationData notification) {
        super(source);
        this.username = username;
        this.notification = notification;
    }

    public String getUsername() {
        return username;
    }

    public NotificationData getNotification() {
        return notification;
    }

}

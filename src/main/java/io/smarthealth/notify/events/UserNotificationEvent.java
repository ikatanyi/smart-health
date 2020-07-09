package io.smarthealth.notify.events;

import io.smarthealth.notify.data.NotificationData;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author Kelsas
 */
public class UserNotificationEvent extends ApplicationEvent {

    private final NotificationData notification;

    public UserNotificationEvent(Object source, NotificationData notification) {
        super(source);
        this.notification = notification;
    }

    public NotificationData getNotification() {
        return notification;
    }

}

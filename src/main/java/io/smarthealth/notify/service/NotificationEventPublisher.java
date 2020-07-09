package io.smarthealth.notify.service;

import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.notify.events.DocRequestEvent;
import io.smarthealth.notify.data.NotificationData;
import io.smarthealth.notify.events.UserNotificationEvent;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kelsas
 */
@Component
public class NotificationEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishUserNotificationEvent(NotificationData notification) {
        UserNotificationEvent event = new UserNotificationEvent(this, notification);
        applicationEventPublisher.publishEvent(event);
    }

    public void publishDocRequestEvent(List<DoctorRequestData.RequestType> requestType) {
        DocRequestEvent requestEvent = new DocRequestEvent(this, requestType);
        applicationEventPublisher.publishEvent(requestEvent);
    }

}

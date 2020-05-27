package io.smarthealth.notifications.service;

import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.notifications.data.RequestCreatedEvent;
import io.smarthealth.notifications.data.RequestUpdatedEvent;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kelsas
 */
@Component
public class RequestEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    void publishEvent(final String name) {

    }

    public void publishUpdateEvent(DoctorRequestData.RequestType requestType) {
        RequestUpdatedEvent requestEvent = new RequestUpdatedEvent(this, requestType);
        applicationEventPublisher.publishEvent(requestEvent);
    }

    public void publishCreateEvent(List<DoctorRequestData.RequestType> requestType) {
        RequestCreatedEvent requestEvent = new RequestCreatedEvent(this, requestType);
        applicationEventPublisher.publishEvent(requestEvent);
    }

}

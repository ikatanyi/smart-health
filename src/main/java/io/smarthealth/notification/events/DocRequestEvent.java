package io.smarthealth.notification.events;

import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import java.util.List;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author Kelsas
 */
public class DocRequestEvent extends ApplicationEvent {

    private final List<RequestType> requestType;

    public DocRequestEvent(Object source, List<RequestType> requestType) {
        super(source);
        this.requestType = requestType;
    }

    public List<RequestType> getRequestType() {
        return requestType;
    }

}

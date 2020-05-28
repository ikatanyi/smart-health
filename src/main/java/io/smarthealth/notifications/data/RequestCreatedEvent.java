/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notifications.data;

import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import java.util.List;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author Kelsas
 */
public class RequestCreatedEvent extends ApplicationEvent {

    private final List<RequestType> requestType;

    public RequestCreatedEvent(Object source, List<RequestType> requestType) {
        super(source);
        this.requestType = requestType;
    }

    public List<RequestType> getRequestType() {
        return requestType;
    }

}

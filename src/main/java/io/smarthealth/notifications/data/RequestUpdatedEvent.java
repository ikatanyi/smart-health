/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notifications.data;

import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author Kelsas
 */
public class RequestUpdatedEvent extends ApplicationEvent {

    private final RequestType requestType;

    public RequestUpdatedEvent(Object source, RequestType requestType) {
        super(source);
        this.requestType = requestType;
    }

    public RequestType getRequestType() {
        return requestType;
    }

}

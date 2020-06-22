/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notifications.events;

import org.springframework.context.ApplicationEvent;

/**
 *
 * @author Kelsas
 */
public class SseKeepAliveEvent extends ApplicationEvent {

    private String keepAliveData;

    public SseKeepAliveEvent(String keepAliveData) {
        super(keepAliveData);
        this.keepAliveData = keepAliveData;
    }

    public String getKeepAliveData() {
        return keepAliveData;
    }

    public void setKeepAliveData(String keepAliveData) {
        this.keepAliveData = keepAliveData;
    }
}

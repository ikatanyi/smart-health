/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notifications.data;

/**
 *
 * @author Kelsas
 */
public class NotificationBlock {

    private Long recipient;
    private Long sender;
    private String message;

    public NotificationBlock() {
    }

    public NotificationBlock(Long recipient, Long sender, String message) {
        this.recipient = recipient;
        this.sender = sender;
        this.message = message;
    }

    public Long getRecipient() {
        return recipient;
    }

    public void setRecipient(Long recipient) {
        this.recipient = recipient;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

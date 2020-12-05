/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.messaging.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Kelsas
 */
@Getter
@Setter
@ToString
public class Mail {

    private String mailFrom;
    private String mailTo;
    private String mailCc;
    private String mailBcc;
    private String mailSubject;
    private String mailContent;
    private String templateName;
    private String contentType;
    private String attachmentName;
    private Object attachment;

    public Mail() {
        this.contentType = "text/html";
    }
}

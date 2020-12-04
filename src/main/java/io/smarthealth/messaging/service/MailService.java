/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.messaging.service;

import io.smarthealth.messaging.model.Mail;
import io.smarthealth.security.domain.User;
import org.springframework.scheduling.annotation.Async;

/**
 *
 * @author Kelsas
 */
public interface MailService {

    @Async
    void sendActivationEmail(User user);

    @Async
    void sendCreationEmail(User user);

    @Async
    void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml);

    @Async
    void sendEmail(Mail message, boolean isHtml);

    @Async
    void sendPasswordResetMail(User user);

}

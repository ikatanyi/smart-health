/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.messaging.service;

import io.smarthealth.messaging.model.EmailData;
import io.smarthealth.security.domain.User;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Locale;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

/**
 * @author Kelsas
 */
@Slf4j
@Service
public class EmailService {

    private static final String USER = "user";
    private static final String BASE_URL = "baseUrl";
    private static final String messageFrom = "smarthealth@localhost";
    private static final String applicationMailBaseUrl = "http://127.0.0.1:8200";
    private static final String contentType = "application/pdf";

    private final JavaMailSender javaMailSender;
    private final MessageSource messageSource;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender javaMailSender, MessageSource messageSource, SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    public void sendMailWithAttachment(
            final String recipientName, final String recipientEmail, final String attachmentFileName,
            final byte[] attachmentBytes, final String attachmentContentType, final Locale locale) throws MessagingException {
        // Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
        message.setSubject("Inventory about to Expire");
        message.setFrom("smarthealthv2@gmail.com");
        message.setTo(recipientEmail);

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.templateEngine.process("mails/test-template", ctx);
        message.setText(htmlContent, true /* isHtml */);

        // Add the attachment
        final InputStreamSource attachmentSource = new ByteArrayResource(attachmentBytes);
        message.addAttachment(attachmentFileName, attachmentSource, attachmentContentType);

        // Send mail
        this.javaMailSender.send(mimeMessage);

    }

    @Async
    public void send(EmailData emailDto) {
        log.info("Sending SMTP mail from thread " + Thread.currentThread().getName());
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(message, true);
            helper.setSubject(emailDto.getSubject());
            helper.setTo(emailDto.getTo());
            helper.setText(emailDto.getMessage(), true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        javaMailSender.send(message);
        log.info("Sent SMTP mail from thread " + Thread.currentThread().getName());
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(messageFrom);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    public void sendEmail(String to, String subject, String content, byte[] attachment, String filename, String contentType, boolean isMultipart, boolean isHtml) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(messageFrom);
            message.setSubject(subject);
            message.setText(content, isHtml);

            // Add the attachment
            final InputStreamSource attachmentSource = new ByteArrayResource(attachment);
            message.addAttachment(filename, attachmentSource, contentType);

            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    public void sendStockExpiryEmail(User user, byte[] attachment) {
        log.debug("Sending stock expirty email to '{}'", user.getEmail());
        String filename = "Stock expiry report " + LocalDateTime.now().toString();

        sendEmailFromTemplate(user, "mail/stockExpiryEmail", "email.stock.expiry.title", attachment, filename);
    }

    @Async
    public void sendStockReorderLevelEmail(User user, byte[] attachment) {
        log.debug("Sending stock reorder level email to '{}'", user.getEmail());
        String filename = "Stock reorder level report " + LocalDateTime.now().toString();
        sendEmailFromTemplate(user, "mail/reorderLevelEmail", "email.reorder.level.title", attachment, filename);
    }

    @Async
    private void sendEmailFromTemplate(User user, String templateName, String titleKey, byte[] attachment, String filename) {
        if (user.getEmail() == null) {
            log.debug("Email doesn't exist for user '{}'", user.getUsername());
            return;
        }
        Locale locale = Locale.forLanguageTag(user.getLangKey() != null ? user.getLangKey() : "en");
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, applicationMailBaseUrl);
        context.setVariable("currentDate", LocalDateTime.now().toString());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(user.getEmail(), subject, content, attachment, filename, contentType, true, true);
    }

    @Async
    public void sendMailWithAttachment(String to, String subject, String body, String fileToAttach, String fileName, String contentType) {
        log.info("START send email with attachment ");
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                mimeMessage.setFrom(messageFrom);
                mimeMessage.setSubject(subject);
                mimeMessage.setText(body);

                FileSystemResource file = new FileSystemResource(new File(fileToAttach));
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//                helper.addAttachment(MimeUtility.encodeText("")), new ByteArrayResource(IOUtils.toByteArray(file)));
                helper.setText("", true);
                helper.addAttachment(fileName, file, contentType);

            }
        };

        try {
            javaMailSender.send(preparator);
        } catch (MailException ex) {
            log.warn("Email could not be sent >> " + ex.getMessage());
        }
    }

}

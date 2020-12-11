///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package io.smarthealth.messaging.service;
//
//import io.smarthealth.messaging.model.DaEmail;
//import java.nio.charset.StandardCharsets;
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.MailException;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import io.smarthealth.messaging.model.Mail;
//import io.smarthealth.security.domain.User;
//import java.io.File;
//import java.io.IOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.activation.DataSource;
//import org.springframework.core.io.ClassPathResource;
//import org.thymeleaf.context.Context;
//
///**
// *
// * @author Kelsas
// */
//@Slf4j
////@Service
//public class SmtpMailService implements MailService {
//
//    private final JavaMailSender emailSender;
//   // private final SpringTemplateEngine templateEngine;
//
//    @Value("${io.smarthealth.mail.from}")
//    private String mailFrom;
//
//    @Value("${io.smarthealth.mail.base-url}")
//    private String mailBaseUrl;
//
//    public SmtpMailService(JavaMailSender javaMailSender){ //, SpringTemplateEngine templateEngine) {
//        this.emailSender = javaMailSender;
////        this.templateEngine = templateEngine;
//    }
//
//    @Async
//    @Override
//    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
//        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
//                isMultipart, isHtml, to, subject, content);
//
//        // Prepare message using a Spring helper
//        MimeMessage mimeMessage = emailSender.createMimeMessage();
//        try {
//            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
//            message.setTo(to);
//            message.setFrom(mailFrom);
//            message.setSubject(subject);
//            message.setText(content, isHtml);
//            emailSender.send(mimeMessage);
//            log.debug("Sent email to User '{}'", to);
//        } catch (MailException | MessagingException e) {
//            log.warn("Email could not be sent to user '{}'", to, e);
//        }
//    }
//
//    @Async
//    @Override
//    public void sendEmail(Mail message, boolean isHtml) {
//
//        MimeMessage emailMessage = emailSender.createMimeMessage();
//        try {
//            MimeMessageHelper mailBuilder = new MimeMessageHelper(emailMessage, true);
//
//            mailBuilder.setTo(message.getMailTo());
//            mailBuilder.setFrom(message.getMailFrom());
//            mailBuilder.setText(message.getMailContent(), isHtml); // Second parameter indicates that this is HTML mail
//            mailBuilder.setSubject(message.getMailSubject());
//
//            if (message.getMailCc() != null && message.getMailCc().length() != 0) {
//                mailBuilder.setCc(message.getMailCc());
//            }
//
//            if (message.getMailCc() != null && message.getMailCc().length() != 0) {
//                mailBuilder.setBcc(message.getMailCc());
//            }
//
//            if (message.getAttachment() != null) {
//                if (message.getAttachment() instanceof DataSource) {
//                    mailBuilder.addAttachment(message.getAttachmentName(), (DataSource) message.getAttachment());
//                }
//                if (message.getAttachment() instanceof File) {
//                    mailBuilder.addAttachment(message.getAttachmentName(), (File) message.getAttachment());
//                }
//            }
//            emailSender.send(emailMessage);
//            log.debug("Sent email to User '{}'", message.getMailTo());
//        } catch (MailException | MessagingException e) {
//            log.warn("Email could not be sent to user '{}'", message.getMailTo(), e);
//        }
//    }
//
//    @Async
//    @Override
//    public void sendActivationEmail(User user) {
//        log.debug("Sending activation email to '{}'", user.getEmail());
//        sendEmailFromTemplate(user, "mail/activationEmail", "SmartHealth account activation");
//    }
//
//    @Async
//    @Override
//    public void sendCreationEmail(User user) {
//        log.debug("Sending creation email to '{}'", user.getEmail());
//        sendEmailFromTemplate(user, "mail/creationEmail", "SmartHealth account activation");
//    }
//
//    @Async
//    @Override
//    public void sendPasswordResetMail(User user) {
//        log.debug("Sending password reset email to '{}'", user.getEmail());
//        sendEmailFromTemplate(user, "mail/passwordResetEmail", "SmartHealth password reset");
//    }
//
//    @Async
//    private void sendEmailFromTemplate(User user, String templateName, String subject) {
//        if (user.getEmail() == null) {
//            log.debug("Email doesn't exist for user '{}'", user.getUsername());
//            return;
//        }
//        Context context = new Context();
//        context.setVariable("user", user);
//        context.setVariable("baseUrl", mailBaseUrl);
////        String content = templateEngine.process(templateName, context);
////        sendEmail(user.getEmail(), subject, content, false, true);
//    }
//
//    @Override
//    public void sendEmail(DaEmail mail) throws MessagingException, IOException {
//        MimeMessage message = emailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
//       helper.addAttachment("template-cover.png", new ClassPathResource("javabydeveloper-email.PNG"));
//
//        if (mail.getAttachments() != null && !mail.getAttachments().isEmpty()) {
//            mail.getAttachments()
//                    .forEach(attach -> {
//                        try {
//                            helper.addAttachment("Expiry reports.pdf", (DataSource) attach);
//                        } catch (MessagingException ex) {
//                            Logger.getLogger(SmtpMailService.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                    );
//        }
//
//        Context context = new Context();
//        context.setVariables(mail.getProps());
//
////        String html = templateEngine.process(mail.getTemplate(), context);
//
//        helper.setTo(mail.getMailTo());
////        helper.setText(html, true);
//        helper.setSubject(mail.getSubject());
//        helper.setFrom(mail.getFrom());
//
//        emailSender.send(message);
//    }
//}

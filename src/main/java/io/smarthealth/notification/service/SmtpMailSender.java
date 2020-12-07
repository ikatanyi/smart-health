package io.smarthealth.notification.service;

import io.smarthealth.notification.data.EmailData;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.DataSource;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Component
public class SmtpMailSender implements EmailerService<EmailData> {

    private final JavaMailSender mailSender;
    private final TemplateEngine textTemplateEngine;
    private final TemplateEngine htmlTemplateEngine;
    private final TemplateEngine fileTemplateEngine;

    public SmtpMailSender(JavaMailSender mailSender, TemplateEngine textTemplateEngine, TemplateEngine htmlTemplateEngine, TemplateEngine fileTemplateEngine) {
        this.mailSender = mailSender;
        this.textTemplateEngine = textTemplateEngine;
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.fileTemplateEngine = fileTemplateEngine;
    }

    /**
     * Sends a mail using a MimeMessageHelper
     *
     * @param emailDto
     * @param mail
     */
//    @Override
//    @Async
//    public void send(EmailData mail) {
//        log.info("Sending SMTP mail from thread " + Thread.currentThread().getName()); // toString gives more info    	
//        // create a mime-message
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper;
//
//        try {
//            // create a helper
//            helper = new MimeMessageHelper(message, true);
//            // set the attributes
//            helper.setSubject(mail.getSubject());
//            helper.setTo(mail.getTo());
//            helper.setText(mail.getBody(), true); // true indicates html
//            // continue using helper object for more functionalities like adding attachments, etc.  
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
//        //send the mail
//        mailSender.send(message);
//        log.info("Sent SMTP mail from thread " + Thread.currentThread().getName());
//    }
    @Override
    @Async
    public void send(EmailData emailDto) {
        log.info("Sending SMTP mail from thread " + Thread.currentThread().getName());
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(message, true);
            helper.setSubject(emailDto.getSubject());
            helper.setTo(emailDto.getTo());
            helper.setText(emailDto.getMessage(), true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        mailSender.send(message);
        log.info("Sent SMTP mail from thread " + Thread.currentThread().getName());
    }

    /**
     * Send emails using templates in Emailer/Templates/ directory
     *
     * @param EmailData
     * @return EmailData
     * @throws MessagingException
     * @throws IOException
     */
    public EmailData sendEmail(EmailData emailDto) throws MessagingException, IOException {

        // Prepare the evaluation context
        Context ctx = prepareContext(emailDto);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        // Prepare message using a Spring helper
        MimeMessageHelper message = prepareMessage(mimeMessage, emailDto);

        // Create the HTML body using Thymeleaf
        String htmlContent = this.fileTemplateEngine.process(emailDto.getTemplateName(), ctx);
        message.setText(htmlContent, true /* isHtml */);
        emailDto.setEmailedMessage(htmlContent);

        log.info("Processing email request: " + emailDto.toString());

        message = prepareStaticResources(message, emailDto);

        // Send mail
        this.mailSender.send(mimeMessage);

        this.fileTemplateEngine.clearTemplateCache();

        return emailDto;

    }

    /**
     * Send email using Text template
     *
     * @param EmailData
     * @return EmailData
     * @throws IOException
     * @throws MessagingException
     */
    public EmailData sendTextTemplateEmail(EmailData emailDto)
            throws IOException, MessagingException {

        // Prepare email context
        Context ctx = prepareContext(emailDto);

        // Prepare message
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        // Prepare message using a Spring helper
        MimeMessageHelper message = prepareMessage(mimeMessage, emailDto);
        // Create email message using TEXT template
        String textContent = this.textTemplateEngine.process(emailDto.getTemplateName(), ctx); // text/email-text\"

        emailDto.setEmailedMessage(textContent);
        message.setText(textContent);

        // Send email
        this.mailSender.send(mimeMessage);

        return emailDto;

    }

    /**
     * Send email with html template found in classpath resource
     *
     * @param EmailData
     * @return EmailData
     * @throws MessagingException
     * @throws IOException
     */
    public EmailData sendHtmlEmail(EmailData emailDto) throws MessagingException, IOException {
        // Prepare the evaluation context
        Context ctx = prepareContext(emailDto);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper message = prepareMessage(mimeMessage, emailDto);

        // Create the HTML body using Thymeleaf
        String htmlContent = this.htmlTemplateEngine.process(emailDto.getTemplateName(), ctx);
        message.setText(htmlContent, true /* isHtml */);
        emailDto.setEmailedMessage(htmlContent);

        log.info("Processing html email request: " + emailDto.toString());

        //message = prepareStaticResources(message, emailDto);
        // Send mail
        this.mailSender.send(mimeMessage);

        this.htmlTemplateEngine.clearTemplateCache();

        return emailDto;

    }

    /**
     * Send multiple emails using templates in Emailer/Templates/ directory
     *
     * @param emailDtos
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<EmailData> sendEmails(List<EmailData> emailDtos) throws MessagingException, IOException {

        List<MimeMessage> mimeMessages = new ArrayList<>();
        MimeMessage mimeMessage = null;

        for (EmailData emailDto : emailDtos) {

            // Prepare the evaluation context
            final Context ctx = prepareContext(emailDto);

            // Prepare message using a Spring helper
            mimeMessage = this.mailSender.createMimeMessage();
            MimeMessageHelper message = prepareMessage(mimeMessage, emailDto);

            // Create the HTML body using Thymeleaf
            String htmlContent = this.fileTemplateEngine.process(emailDto.getTemplateName(), ctx);
            message.setText(htmlContent, true /* isHtml */);
            emailDto.setEmailedMessage(htmlContent);

            log.info("Processing emails request: " + emailDto.toString());

            message = prepareStaticResources(message, emailDto);

            mimeMessages.add(mimeMessage);
        }

        // Send mail
        this.mailSender.send(mimeMessages.toArray(new MimeMessage[0]));

        this.fileTemplateEngine.clearTemplateCache();

        return emailDtos;

    }

    private MimeMessageHelper prepareMessage(MimeMessage mimeMessage, EmailData emailDto) throws MessagingException, IOException {

        // Prepare message using a Spring helper
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
        message.setSubject(emailDto.getSubject());
        message.setFrom(emailDto.getFrom());
        message.setTo(emailDto.getTo());

        if (emailDto.getCc() != null && emailDto.getCc().length != 0) {
            message.setCc(emailDto.getCc());
        }

        if (emailDto.getBcc() != null && emailDto.getBcc().length != 0) {
            message.setBcc(emailDto.getBcc());
        }

        if (emailDto.isHasAttachment()) {
            if (emailDto.getAttachmentFile() != null) {
                message.addAttachment(emailDto.getAttachmentName(), (DataSource) emailDto.getAttachmentFile());
            } else {

                List<File> attachments = loadResources(emailDto.getPathToAttachment() + "/*" + emailDto.getAttachmentName() + "*.*");
                for (File file : attachments) {
                    message.addAttachment(file.getName(), file);
                }
            }
        }

        return message;

    }

    private List<File> loadResources(String fileNamePattern) throws IOException {
        PathMatchingResourcePatternResolver fileResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = null;

        try {
            resources = fileResolver.getResources("file:" + fileNamePattern);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<File> attachFiles = new ArrayList<>();

        for (Resource resource : resources) {
            attachFiles.add(resource.getFile());
        }

        return attachFiles;

    }

    private Context prepareContext(EmailData emailDto) {
        // Prepare the evaluation context
        Context ctx = new Context();
        Set<String> keySet = emailDto.getParameterMap().keySet();
        keySet.forEach(s -> {
            ctx.setVariable(s, emailDto.getParameterMap().get(s));
        });

        Set<String> resKeySet = emailDto.getStaticResourceMap().keySet();
        resKeySet.forEach(s -> {
            ctx.setVariable(s, emailDto.getStaticResourceMap().get(s));
        });

        return ctx;
    }

    private MimeMessageHelper prepareStaticResources(MimeMessageHelper message,
            EmailData emailDto) throws MessagingException {
        Map<String, Object> staticResources = emailDto.getStaticResourceMap();

        for (Map.Entry<String, Object> entry : staticResources.entrySet()) {

            ClassPathResource imageSource
                    = new ClassPathResource("static/" + (String) entry.getValue());
            message.addInline(entry.getKey(), imageSource, "image/png");
            message.addInline((String) entry.getValue(), imageSource, "image/png");

        }

        return message;
    }
}

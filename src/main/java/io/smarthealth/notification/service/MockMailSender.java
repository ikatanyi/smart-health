package io.smarthealth.notification.service;

import io.smarthealth.notification.data.EmailData;
import io.smarthealth.notification.service.EmailerService;
import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
 
@Slf4j
public class MockMailSender implements EmailerService<EmailData> {

    public MockMailSender() {
        log.info("Created");
    }

//    @Override
//    public void send(EmailData mail) {
//
//        log.info("Sending mail to " + mail.getTo());
//        log.info("Subject: " + mail.getSubject());
//        log.info("Body: " + mail.getBody());
//    }

    @Override
    public void send(EmailData emailDto){
        log.info("Simple Email Initiated.. ");
        log.info("Sending mail to " + emailDto.toString());
    }

    @Override
    public EmailData sendEmail(EmailData emailDto) throws MessagingException, IOException {
        log.info("Send emails using templates in Emailer/Templates/ directory");
        log.info("Sending mail to " + emailDto.toString());
        return emailDto;
    }

    @Override
    public EmailData sendTextTemplateEmail(EmailData emailDto) throws IOException, MessagingException {
        log.info("Send email using Text template");
        log.info("Sending mail to " + emailDto.toString());
        return emailDto;
    }

    @Override
    public EmailData sendHtmlEmail(EmailData emailDto) throws MessagingException, IOException {
        log.info("Send email with html template found in classpath resource");
        log.info("Sending mail to " + emailDto.toString());
        return emailDto;
    }

    @Override
    public List<EmailData> sendEmails(List<EmailData> emailDtos) throws MessagingException, IOException {
        log.info("Send multiple emails using templates in Emailer/Templates/ directory");
        emailDtos.forEach((emailDto) -> {
            log.info("Sending mail to " + emailDto.toString());
        });
        return emailDtos;
    }

}

package io.smarthealth.notification.api;

import io.smarthealth.notification.data.EmailData;
import io.smarthealth.notification.service.EmailerService;
import io.swagger.annotations.Api;
import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping("/api")
public class EmailerController {

    private final EmailerService emailerService;

    public EmailerController(EmailerService emailerService) {
        this.emailerService = emailerService;
    }

    @PostMapping(value = "/email")
    public void send(@RequestBody EmailData email) throws IOException, MessagingException {
        emailerService.send(email);
    }

    @PostMapping(value = "/sendemail")
    public EmailData emailHtmlTemplate(@RequestBody EmailData email) throws IOException, MessagingException {
        emailerService.sendEmail(email);
        return email;
    }

    @PostMapping(value = "/sendtextemail")
    public EmailData emailTextTemplate(@RequestBody EmailData email) throws IOException, MessagingException {
        emailerService.sendTextTemplateEmail(email);
        return email;
    }

    @PostMapping(value = "/sendhtmlemail")
    public EmailData emailInHtmlTemplate(@RequestBody EmailData email) throws IOException, MessagingException {
        emailerService.sendHtmlEmail(email);
        return email;
    }

    @PostMapping(value = "/sendemails")
    public Iterable<EmailData> emailHtmlTemplate(@RequestBody List<EmailData> emails) throws IOException, MessagingException {
        emailerService.sendEmails(emails);
        return emails;
    }
}

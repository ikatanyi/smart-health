package io.smarthealth.notification.service;

import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;

/**
 * The mail sender interface for sending mail
 *
 * @param <EmailDto>
 */
public interface EmailerService<EmailDto> {
 
    public void send(EmailDto emailDto);

    public EmailDto sendEmail(EmailDto emailDto) throws MessagingException, IOException;

    public EmailDto sendTextTemplateEmail(EmailDto emailDto) throws IOException, MessagingException;

    public EmailDto sendHtmlEmail(EmailDto emailDto) throws MessagingException, IOException;

    public List<EmailDto> sendEmails(List<EmailDto> emailDtos) throws MessagingException, IOException;

}

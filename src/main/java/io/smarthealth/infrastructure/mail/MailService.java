package io.smarthealth.infrastructure.mail;

/**
 * The mail sender interface for sending mail
 * @param <EmailData>
 */
public interface MailService<EmailData> {

    void send(EmailData mail);    
}

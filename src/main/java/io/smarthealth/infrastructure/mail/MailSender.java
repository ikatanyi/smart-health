package io.smarthealth.infrastructure.mail;

/**
 * The mail sender interface for sending mail
 * @param <MailData>
 */
public interface MailSender<MailData> {

    void send(MailData mail);
}

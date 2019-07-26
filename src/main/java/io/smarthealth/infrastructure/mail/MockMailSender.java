package io.smarthealth.infrastructure.mail;

import lombok.extern.slf4j.Slf4j;

/**
 * A mock mail sender for writing the mails to the log.
 *
 * @author Sanjay Patel
 */
@Slf4j
public class MockMailSender implements MailSender<EmailData> {

    public MockMailSender() {
        log.info("Created");
    }

    @Override
    public void send(EmailData mail) {

        log.info("Sending mail to " + mail.getTo());
        log.info("Subject: " + mail.getSubject());
        log.info("Body: " + mail.getBody());
    }

}

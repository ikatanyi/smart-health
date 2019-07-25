package io.smarthealth.infrastructure.mail;

import lombok.Getter;
import lombok.Setter;

/**
 * Data needed for sending a mail. Override this if you need more data to be
 * sent.
 */
@Getter
@Setter
public class ApplicationMailData {

    private String to;
    private String subject;
    private String body;

    public static ApplicationMailData of(String to, String subject, String body) {

        ApplicationMailData data = new ApplicationMailData();

        data.to = to;
        data.subject = subject;
        data.body = body;

        return data;
    }
}

package io.smarthealth.infrastructure.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Data needed for sending a mail. Override this if you need more data to be
 * sent.
 */
@Getter
@Setter
@Component
public class EmailData {
     @Autowired
    private Environment env;
     
    private String to;
    private String subject;
    private String body;
    private String from;

    public static EmailData of(String to, String subject, String body) {

        EmailData data = new EmailData();

        data.to = to;
        data.subject = subject;
        data.body = body;
       
        return data;
    }
}

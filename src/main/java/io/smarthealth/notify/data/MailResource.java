package io.smarthealth.notify.data;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class MailResource {

    @NotNull
    private String subject;

    @NotNull
    private String message;

    @NotNull
    private String recipient;
}

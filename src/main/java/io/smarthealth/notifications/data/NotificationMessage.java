package io.smarthealth.notifications.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class NotificationMessage {

    private Long msgId;
    private String message;
    private String reference;
}

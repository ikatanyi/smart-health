/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notifications.data;

import io.smarthealth.notifications.domain.*;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class NotificationData {

    private long notificationId;
    private Long senderId;
    private String senderName;
    private Long recipientId;
    private String recipientName;
    private NotificationType notificationType;
    private NotificationMessage message;
}

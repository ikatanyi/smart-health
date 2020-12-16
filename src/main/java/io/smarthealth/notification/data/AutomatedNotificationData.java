/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notification.data;

import io.smarthealth.notification.domain.AutomatedNotification;
import io.smarthealth.notification.domain.enumeration.NotificationType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class AutomatedNotificationData {

    private Long id;
    private NotificationType notificationType;
    private List<NoticeUser> users = new ArrayList<>();

    private String notificationParameter; // hours
    private boolean active;

    public static AutomatedNotificationData map(AutomatedNotification automatedNotification) {
        AutomatedNotificationData data = new AutomatedNotificationData();
        data.setActive(automatedNotification.isActive());
        data.setId(automatedNotification.getId());
        data.setNotificationParameter(automatedNotification.getNotificationParameter());
        data.setNotificationType(automatedNotification.getNotificationType());
        data.setUsers(
                automatedNotification.getUsers().stream()
                        .map(NoticeUser::map)
                        .collect(Collectors.toList())
        );

        return data;
    }
}

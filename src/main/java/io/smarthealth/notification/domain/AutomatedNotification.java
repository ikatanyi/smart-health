/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notification.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.notification.domain.enumeration.NotificationType;
import io.smarthealth.security.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "app_automated_notifications")
public class AutomatedNotification extends Identifiable {

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "app_automated_notifications_users",
            joinColumns = {
                @JoinColumn(name = "automated_notification_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_auto_notice_id"))},
            inverseJoinColumns = {
                @JoinColumn(name = "users_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_auto_user_id"))})
    private List<User> users = new ArrayList<>();

    private String notificationParameter; // hours

    private boolean active;

    public void addUser(User user) {
        if (!this.getUsers().contains(user)) {
            this.getUsers().add(user);
        }
    }

    public void removeUser(User user) {
        users.remove(user);
    }
}

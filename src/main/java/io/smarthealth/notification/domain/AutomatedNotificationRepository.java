/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notification.domain;

import io.smarthealth.notification.domain.enumeration.NotificationType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface AutomatedNotificationRepository extends JpaRepository<AutomatedNotification, Long> {

    Optional<AutomatedNotification> findByNotificationType(NotificationType type);
}

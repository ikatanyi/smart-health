 package io.smarthealth.notifications.domain;

import io.smarthealth.security.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipient(User recipient, Pageable pageable);

    Page<Notification> findByType(NotificationType type, Pageable pageable);
}

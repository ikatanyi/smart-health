package io.smarthealth.notify.domain;

import io.smarthealth.notify.data.NoticeType;
import io.smarthealth.security.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface NotificationRepository extends JpaRepository<Notifications, Long>, JpaSpecificationExecutor<Notifications> {

    List<Notifications> findByRecipient(User user);

    List<Notifications> findByRecipientAndIsRead(User user, boolean read);

    long countByRecipientAndIsRead(User user, boolean read);

    Optional<Notifications> findByRecipientAndReferenceAndNoticeType(User recipient, String reference, NoticeType noticeType);
    
    @Modifying
    @Query(value = "UPDATE user_notifications u SET u.is_read=TRUE WHERE u.is_read=FALSE AND u.recipient_id=:recipient", nativeQuery = true)
    int readAllNotifications(@Param("recipient") Long id);
}

package io.smarthealth.notification.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.notification.data.NoticeType;
import io.smarthealth.notification.data.NotificationData;
import io.smarthealth.security.domain.User;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Kelsas
 */ 
@Entity 
@Data
@Table(name = "user_notifications") 
public class Notification extends Auditable {

    private LocalDateTime datetime;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_notify_user_id"))
    private User recipient;
    private String message;
    private boolean isRead;
    private String reference;
    @Enumerated(EnumType.STRING)
    private NoticeType noticeType;

    public Notification() {
    }

    public Notification(User recipient, String message, NoticeType noticeType,String reference) {
        this.recipient = recipient;
        this.message = message;
        this.isRead = false;
        this.noticeType = noticeType;
        this.datetime = LocalDateTime.now();
        this.reference = reference;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.getId());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Notification other = (Notification) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Notice{" + "recipient=" + recipient + ", message=" + message + ", isRead=" + isRead + '}';
    }

    public NotificationData toData() {
        return NotificationData.builder()
                .id(this.getId())
                .datetime(this.datetime)
                .isRead(this.isRead)
                .description(this.message)
                .noticeType(this.noticeType)
                .userId(this.recipient.getId())
                .username(this.recipient.getUsername())
                .name(this.recipient.getName())
                .reference(this.reference)
                .title(this.noticeType.getLabel())
                .user(this.recipient)
                .build();

    }
}

package io.smarthealth.notifications.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.security.domain.User;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
public class Notification extends Auditable {

    @NotNull
    private boolean isRead;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @NotNull
    private NotificationType type;

    private String reference;

}

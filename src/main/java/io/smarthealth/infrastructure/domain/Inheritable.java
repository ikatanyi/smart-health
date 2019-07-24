package io.smarthealth.infrastructure.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Version;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 *
 * @author Kelsas
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Inheritable implements Serializable {

    @Id
    @Column(length = 38, unique = true)
    private String id;

    @Column(length = 38)
    private String companyId;
    @Version
    private Long version;
    @CreatedDate
    protected Instant createdOn;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    protected Instant lastModifiedOn;
    @LastModifiedBy
    private String lastModifiedBy;

    @PrePersist
    public void autofill() {
        String ids = UUID.randomUUID().toString();
        this.setId(ids);
    }
}

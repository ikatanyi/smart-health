package io.smarthealth.common.domain;

import java.time.Instant;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
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
public abstract class Auditable extends Identifiable {

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
}

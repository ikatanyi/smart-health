package io.smarthealth.infrastructure.domain;

import java.time.Instant;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Audit Model 
 * @author Kelsas
 */
//@Data
@MappedSuperclass
//@Audited
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(Instant lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
    
    
}

package io.smarthealth.common.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
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
public abstract class UuidIdentifiable implements Serializable{
 
    @Id
    @Column(length = 38, unique = true)
    private String uuid;

    @PrePersist
    public void autofill() {
        String ids=UUID.randomUUID().toString(); 
        this.setUuid(ids);
    }
    
    @CreatedDate
    protected Instant createdOn;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    protected Instant lastModifiedOn;
    @LastModifiedBy
    private String lastModifiedBy;
}

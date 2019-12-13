package io.smarthealth.infrastructure.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

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

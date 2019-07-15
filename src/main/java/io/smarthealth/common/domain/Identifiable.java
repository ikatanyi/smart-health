package io.smarthealth.common.domain;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import lombok.Data;

/**
 *  Base Entity using {@link  GeneratedValue } Identity Strategy to generate a primary key, with a unique UUID, User and Date audit information.
 * 
 * @author Kelsas
 */
@Data
@MappedSuperclass
public abstract class Identifiable implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 38, unique = true)
    private String uuid;

    @PrePersist
    public void autofill() {
        String ids=UUID.randomUUID().toString(); 
        this.setUuid(ids);
    }
}

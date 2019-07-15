package io.smarthealth.person.domain;

import io.smarthealth.common.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data; 

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_portrait")
public class Portrait extends Identifiable{
 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Person person;
    
    @Lob
    private byte[] image;
    private Long size;
    private String contentType;
 
}

package io.smarthealth.organization.person.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.*;

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
    private String imageUrl;
    private String imageName;
 
}

package io.smarthealth.infrastructure.image;

import io.smarthealth.clinical.radiology.domain.PatientScanTest;
import io.smarthealth.organization.person.domain.*;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data; 

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "clinical_image")
public class ClinicalImage extends Identifiable{
// 
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    private PatientScanTest pscanTest;
//    
    private Long size;
    private String contentType;
    private String imageUrl;
    private String imageName;
 
}

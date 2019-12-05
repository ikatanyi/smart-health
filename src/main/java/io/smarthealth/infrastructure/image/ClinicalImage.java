package io.smarthealth.infrastructure.image;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
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

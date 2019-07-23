package io.smarthealth.company.person.patient.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *  Patient Allergy Record
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_allergies")
public class Allergy extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    private String allergy;  
    private String reaction;  
    private String severiity; //Critical | Mild | Danger
    private String notes;
    private LocalDateTime dateRecorded = LocalDateTime.now();

}

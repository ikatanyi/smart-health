package io.smarthealth.clinical.record.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Type;

/**
 * Patient PatientDiagnosis Record
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_diagnosis")
public class PatientDiagnosis extends ClinicalRecord {

    @Embedded
    private Diagnosis diagnosis;

    @Column(length = 25)
    private String certainty;

    @Column(length = 25)
    private String diagnosisOrder;

    @Type(type = "text")
    private String notes;

    private Boolean isCondition = Boolean.FALSE;
    
    private LocalDateTime date;
}

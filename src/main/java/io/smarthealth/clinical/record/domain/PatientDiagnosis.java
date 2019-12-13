package io.smarthealth.clinical.record.domain;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

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
}

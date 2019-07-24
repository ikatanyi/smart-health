package io.smarthealth.clinical.record.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

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
}

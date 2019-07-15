package io.smarthealth.clinical.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 * Patient Diagnosis Record
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_diagnosis")
public class Diagnosis extends ClinicalRecord {

    private String code;

    private String description;

    @Column(length = 25)
    private String certainty;

    @Column(length = 25)
    private String diagnosisOrder;
}

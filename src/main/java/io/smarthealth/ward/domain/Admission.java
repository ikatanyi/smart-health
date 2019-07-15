package io.smarthealth.ward.domain;

import io.smarthealth.organization.domain.Bed;
import io.smarthealth.clinical.domain.Diagnosis;
import io.smarthealth.visit.domain.Visit;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Kelsas
 */
@Entity
@Table(name = "patient_admissions")
public class Admission extends Visit {

    private Bed bed;
    @Column(length = 50)
    private String admissionType;   
     //reason for admission
    private String diagnosisCode;
    private String admissionReason;
    
}

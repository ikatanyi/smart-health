 package io.smarthealth.clinical.record.domain;

import io.smarthealth.clinical.admission.data.DischargeDiagnosis;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    
    private String doctor;

    public DischargeDiagnosis toData() {
        DischargeDiagnosis data = new DischargeDiagnosis();
        data.setId(this.getId());
        data.setAdmissionNumber(this.getVisit() != null ? this.getVisit().getVisitNumber() : null);
        data.setCertainty(this.certainty);
        data.setCode(this.diagnosis.getCode());
        data.setCondition(this.isCondition);
        data.setDescription(this.diagnosis.getDescription());
        data.setDiagnosisDate(this.getDateRecorded() != null ? this.getDateRecorded().toLocalDate() : null);
        data.setDiagnosisOrder(this.diagnosisOrder);
        data.setPatientName(this.getPatient() != null ? this.getPatient().getFullName() : null);
        data.setPatientNumber(this.getPatient() != null ? this.getPatient().getFullName() : null);
        data.setRemarks(this.notes);
        
        String done = this.doctor !=null ?  this.doctor : this.getCreatedBy()+ " - " + (this.getDateRecorded() != null ? this.getDateRecorded() : LocalDateTime.ofInstant(this.getCreatedOn(), ZoneId.systemDefault()));
        data.setDoneBy(done);
        return data;
    }
} 

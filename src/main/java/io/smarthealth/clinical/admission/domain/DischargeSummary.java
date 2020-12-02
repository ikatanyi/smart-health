/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.domain;

import io.smarthealth.clinical.admission.data.DischargeData;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_discharge") 
public class DischargeSummary extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_discharge_patient_id"))
    private Patient patient;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_discharge_admission_id"))
    private Admission admission;
    private LocalDateTime dischargeDate;
    private String doctor;
    private String dischargeNo;
    private String dischargeMethod;
    private String dischargedBy;
    private String diagnosis;
    private String instructions;
    private String outcome;
    private LocalDate reviewDate;

    public DischargeData toData() {
        DischargeData data = new DischargeData();
        data.setId(this.getId());
        data.setAdmissionDate(this.admission.getAdmissionDate());
        data.setAdmissionNumber(this.admission.getAdmissionNo());
        data.setDoctor(this.doctor);
        data.setDiagnosis(this.diagnosis);
        data.setDischargeDate(this.dischargeDate);
        data.setDischargeMethod(this.dischargeMethod);
        data.setDischargeNo(this.dischargeNo);
        data.setDischargedBy(this.dischargedBy);
        data.setGender(this.patient.getGender().name());
        data.setInstructions(this.instructions);
        data.setOutcome(this.outcome);
        data.setPatientName(this.patient.getFullName());
        data.setPatientNumber(this.patient.getPatientNumber());
        data.setResidence(this.getPatient().getResidence());
        data.setAge(this.getPatient().getAge());
        data.setReviewDate(this.getReviewDate());
        if(this.getAdmission()!=null){
            if(this.getAdmission().getBed()!=null)
               data.setBed(this.getAdmission().getBed().getName());
            if(this.getAdmission().getWard()!=null)
               data.setWard(this.getAdmission().getWard().getName());
        }
        return data;
    }
}

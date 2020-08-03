/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    private String dischargeNo;
    private String dischargeMethod;
    private String requestedBy; //who requested for discharge
    private Employee doctor; //doctor discharging
    private String diagnosis;
    private String otherIllness;
    private String management;
    private String investigations;
    private String instructions;
    private String clinicalSummary;
    private String recommendations;
    
    
    
}

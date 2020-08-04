/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
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
@Table(name = "patient_nursing_careplan")
public class NursingCarePlan extends Auditable{
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_nursing_care_plan_patient_id"))
    private Patient patient;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_nursing_care_plan_admission_id"))
    private Admission admission;
    private LocalDateTime datetime;
    private String diagnosis;
    private String expectedOutcome;
    private String planOfCare;
    private String intervention;
    private String evaluation;
    private String doneBy;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.inpatient.referral.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.clinical.inpatient.admission.domain.Admission;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
//@Entity
//@Table(name = "patient_referrals")
public class Referral extends Auditable {

    public enum Type {
        Internal,
        External
    }

    public enum Priority {
        Normal,
        Urgent
    }
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_referrals_patient_id"))
    @ManyToOne
    private Patient patient;
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_referrals_medic_id"))
    @ManyToOne
    private Employee medic;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String referralTo;
    private String reason;
    private LocalDate referralDate;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_referrals_admission_id"))
    @ManyToOne
    private Admission relatedEncounter;
    private String referralNotes;
    private String diagnosis;
//    private PatientAttachment attachment;
}

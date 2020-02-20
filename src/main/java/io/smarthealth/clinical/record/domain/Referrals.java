/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain;

import io.smarthealth.clinical.record.data.enums.ReferralType;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
@Entity
public class Referrals extends Auditable {

    @Enumerated(EnumType.STRING)
    private ReferralType referralType;
    @ManyToOne
    @JoinColumn(name = "doctor_id", foreignKey = @ForeignKey(name = "fk__doctor_id_referrals"))
    private Employee doctor;

    @ManyToOne
    @JoinColumn(name = "visit_id", foreignKey = @ForeignKey(name = "fk_visit_id_referrals"))
    private Visit visit;

    @ManyToOne
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_patient_id_referrals"))
    private Patient patient;

    private String doctorName;
    private String doctorSpeciality;
    private String referralNotes;

    private boolean includeVisitClinalNotes;
    private String chiefComplaints;
    private String examinationNotes;

}

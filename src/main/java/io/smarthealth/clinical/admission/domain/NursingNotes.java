/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.domain;

import io.smarthealth.infrastructure.domain.Auditable;
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
@Table(name = "patient_nursing_notes")
public class NursingNotes extends Auditable{
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_nursing_notes_patient_id"))
    private Patient patient;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_nursing_notes_admission_id"))
    private Admission admission;
    private LocalDateTime datetime;
    private String notes;
    private String status;
    private String notesBy;
}

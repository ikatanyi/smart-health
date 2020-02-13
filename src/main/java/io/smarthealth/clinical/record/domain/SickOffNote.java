/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class SickOffNote extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY/*, optional = false*/)
    @JoinColumn(name = "visit_id", foreignKey = @ForeignKey(name = "fk_visit_sick_off_note"))
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY/*, optional = false*/)
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_patient_sick_off_note"))
    private Patient patient;

    private String sickOffNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate reviewDate;
    
    private String recommendation;
    private String reason;

}

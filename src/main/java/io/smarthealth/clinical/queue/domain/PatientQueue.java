/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.queue.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.person.patient.domain.Patient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
@Entity
@Table(name = "patient_queue")
@Inheritance(strategy = InheritanceType.JOINED)
public class PatientQueue extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_que_patient_id"))
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) 
    @JoinColumn(name = "visit_id", foreignKey = @ForeignKey(name = "fk_que_visit_id"))
    private Visit visit;

    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) 
     @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_que_dept_id"))
    private Department department;

}

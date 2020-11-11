/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.queue.domain;

import io.smarthealth.administration.servicepoint.domain.ServicePoints;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.time.Instant;
import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

/**
 *
 * @author Simon.waweru
 */
@Data
@Entity
@Table(name = "patient_queue")
@Inheritance(strategy = InheritanceType.JOINED)
public class PatientQueue extends Auditable {

    public enum QueueUrgency {
        Normal,
        Medium,
        High
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_que_patient_id"))
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "visit_id", foreignKey = @ForeignKey(name = "fk_que_visit_id"))
    private Visit visit;

    private boolean status;

    @Enumerated(EnumType.STRING)
    private QueueUrgency urgency;

//    @ManyToOne(fetch = FetchType.LAZY, optional = true)
//    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_que_dept_id"))
//    private Department department;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "service_point_id", foreignKey = @ForeignKey(name = "fk_que_service_point_id"))
    private ServicePoints servicePoint;

    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "staff_number", foreignKey = @ForeignKey(name = "fk_que_staff_number"))
    private Employee staffNumber;

    @Type(type = "text")
    private String specialNotes;
    
    private Instant stopTime;

}

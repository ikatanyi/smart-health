/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.queue.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.Employee;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

/**
 *
 * @author Simon.Waweru
 */
@Data
@Entity
@Table(name = "patient_queue_log")
public class PatientQueueLog extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "visit_id", foreignKey = @ForeignKey(name = "fk_que_log_visit_id"))
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_que_log_department_id"))
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "service_provider_id", foreignKey = @ForeignKey(name = "fk_que_log_service_provider_id"))
    private Employee serviceProvider;

    
}

package io.smarthealth.clinical.visit.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.person.patient.domain.Patient;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Patient CheckIn
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "patient_visit")
@Inheritance(strategy = InheritanceType.JOINED)
public class Visit extends Auditable {

    public enum Status {
        CheckIn,
        CheckOut,
        Admitted,
        Transferred,
        Discharged
    }

    public enum VisitType {
        Outpatient,
        Inpatient
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_visit_patient_id"))
    private Patient patient;

    @NaturalId
    @Column(length = 38, unique = true)
    private String visitNumber;

    @ManyToOne(fetch = FetchType.LAZY/*, optional = false*/)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_visit_department_id"))
    private Department department;

    private LocalDateTime startDatetime;
    private LocalDateTime stopDatetime;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private VisitType visitType; //Outpatient | Hospitalization

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Status status;

    private Boolean scheduled;

}

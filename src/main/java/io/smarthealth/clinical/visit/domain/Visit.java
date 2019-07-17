package io.smarthealth.clinical.visit.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

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
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @NaturalId
    @Column(length = 38, unique = true)
    private String visitNumber;

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

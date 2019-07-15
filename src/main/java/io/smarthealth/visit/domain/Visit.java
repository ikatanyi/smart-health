package io.smarthealth.visit.domain;

import io.smarthealth.common.domain.Auditable;
import io.smarthealth.patient.domain.Patient;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *  Patient CheckIn 
 * @author Kelsas
 */ 
@Data
@Entity
@Table(name = "patient_visit")
@Inheritance(strategy = InheritanceType.JOINED)
public class Visit extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(length = 38, unique = true)
    private String visitNumber;
    
    private LocalDateTime startDatetime;
    private LocalDateTime stopDatetime;
    @Column(length = 50)
    private String visitType; //Outpatient | Hospitalization
    @Column(length = 50)
    private String status; // CheckIn | CheckOut | Admitted | Transferred | Discharged | 
    private Boolean scheduled;
}

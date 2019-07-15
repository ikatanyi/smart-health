package io.smarthealth.clinical.domain;

import io.smarthealth.common.domain.Auditable;
import io.smarthealth.product.domain.Product;
import io.smarthealth.patient.domain.Patient;
import io.smarthealth.person.domain.Person;
import io.smarthealth.visit.domain.Visit;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Doctor's Requests
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "patient_doctor_request")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DoctorRequest extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    @ManyToOne
    @JoinColumn(name = "visit_id")
    private Visit visit;
    
    @OneToOne
    private Product product;  
    
    @OneToOne
    private Person requestedBy;
    private LocalDateTime orderDatetime;
    private String urgency;
    private String orderNumber;
    private String action;
    private String notes;
    private String fulfillerStatus;
    private String fulfillerComment;
    private Boolean drug;
}

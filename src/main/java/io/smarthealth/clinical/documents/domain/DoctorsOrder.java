package io.smarthealth.clinical.documents.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.product.domain.Product;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.domain.Person;
import io.smarthealth.clinical.visit.domain.Visit;
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
public abstract class DoctorsOrder extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    @ManyToOne
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @ManyToOne
    private Product product;

    @OneToOne
    private Employee requestedBy;
    private LocalDateTime orderDatetime;
    private String urgency;
    private String orderNumber;
    private String action;
    private String notes;
    private String fulfillerStatus;
    private String fulfillerComment;
    private Boolean drug;
}

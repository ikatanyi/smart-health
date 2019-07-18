package io.smarthealth.clinical.documents.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.product.domain.Product;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.clinical.visit.domain.Visit;
import java.time.LocalDateTime;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

/**
 * Doctor's Requests
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "patient_doctor_request")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="order_type")
public abstract class DoctorsOrder extends Auditable {

    //the doctors oders
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
    @NaturalId
    private String orderNumber;
    private String action;
    private String notes;
    private String fulfillerStatus;  //this is the va
    private String fulfillerComment;
    private Boolean drug;
}

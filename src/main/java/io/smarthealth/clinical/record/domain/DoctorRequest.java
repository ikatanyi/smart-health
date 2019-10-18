package io.smarthealth.clinical.record.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDateTime;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
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
public abstract class DoctorRequest extends Auditable {

    //the doctors oders
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_doc_request_patient_id"))
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "visit_id", foreignKey = @ForeignKey(name = "fk_doc_request_visit_id"))
    private Visit visit;

    @ManyToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_doc_request_item_id"))
    private Item item;

    @ManyToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_doc_request_empoyee_id"))
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

package io.smarthealth.clinical.record.domain;

import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.security.domain.User;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDate;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Doctor's Requests
 *
 * @author Kelsas
 */
@Getter
@Setter
@Entity
@Table(name = "patient_doctor_request")
@Inheritance(strategy = InheritanceType.JOINED)
public class DoctorRequest extends Auditable {

    @Enumerated(EnumType.STRING)
    private RequestType requestType;

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
    private User requestedBy;

    private LocalDate orderDate;
    private String urgency;
    private String orderNumber;
    private String notes;
    @Enumerated(EnumType.STRING)
    private FullFillerStatusType fulfillerStatus;  //this is the va
    private String fulfillerComment;
    private Boolean drug;
    private Boolean voided=Boolean.FALSE;

    @Transient
    private String patientNumber;
    @Transient
    private String visitNumber;

    private double itemCostRate;
    private double itemRate; 
}

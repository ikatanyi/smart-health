package io.smarthealth.clinical.admission.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.security.domain.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;

@Entity
@Data
public class AdmissionRequest extends Auditable {


    //the doctors oders
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_admission_request_patient_id"))
    private Patient patient;


    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_admission_request_empoyee_id"))
    private User requestedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDateTime requestDate;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_admission_request_requested_ward"))
    private Ward ward;

    @ManyToOne
    @JoinColumn(name = "visit_id", foreignKey = @ForeignKey(name = "fk_admission_request_opvisit_id"))
    private Visit opVisit;

    private String urgency;
    private String orderNumber;
    private String notes;
    @Enumerated(EnumType.STRING)
    private FullFillerStatusType fulfillerStatus;
    private String fulfillerComment;
    private Boolean voided = Boolean.FALSE;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_admission_request_fulfilled_by_id"))
    private User fullfilledBy;

    private LocalDateTime admissionDateTime;
}

package io.smarthealth.clinical.record.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.security.domain.User;
import java.time.LocalDateTime;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import lombok.Data;

/**
 * Base for all Clinical Documentations of Patient Medical Records
 *
 * @author Kelsas
 */
@Data
@MappedSuperclass
public abstract class ClinicalRecord extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_clinical_record_patient_id"))
    private Patient patient;
    @ManyToOne
//    @JoinColumn(name = "visit_id", foreignKey = @ForeignKey(name = "fk_clinical_record_visit_id"))
    private Visit visit;
    @ManyToOne
    private User healthProvider;
    private LocalDateTime dateRecorded;

    private boolean voided = false;
    private String voidedBy;
    private LocalDateTime voidedDate;

}

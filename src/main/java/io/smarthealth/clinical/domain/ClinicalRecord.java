package io.smarthealth.clinical.domain;

import io.smarthealth.common.domain.Auditable;
import io.smarthealth.organization.domain.Employee;
import io.smarthealth.patient.domain.Patient;
import io.smarthealth.visit.domain.Visit;
import java.time.LocalDateTime;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
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
    private Patient patient;
    @ManyToOne
    private Visit visit;
    @OneToOne
    private Employee healthcareProvifder;
    private LocalDateTime dateRecorded;
    private boolean voided = false;
    private String voidedBy;
    private LocalDateTime voidedDate;

}

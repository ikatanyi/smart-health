package io.smarthealth.clinical.visit.domain;

import io.smarthealth.clinical.record.domain.Diagnosis;
import io.smarthealth.organization.facility.domain.Bed;
import io.smarthealth.organization.facility.domain.Employee;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Inpatient {@link Visit}
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_admissions")
public class Admission extends Visit {

    public enum Type {
        Elective,
        Routine,
        Urgent,
        Maternity,
        Emergency
    }

    @OneToOne
    private Bed bed;
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private Type admissionType;
    @Embedded
    private Diagnosis provisionDiagnosis;
    @OneToOne
    private Employee admittingDoctor;
    private LocalDate admissionDate;
    private LocalDate expectedDischarge;
    private String specialComments;

}

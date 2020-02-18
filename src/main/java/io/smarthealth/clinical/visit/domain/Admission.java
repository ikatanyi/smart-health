package io.smarthealth.clinical.visit.domain;

import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import io.smarthealth.organization.facility.domain.Bed;
import io.smarthealth.organization.facility.domain.Employee;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
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
    @OneToMany
    private List<PatientDiagnosis> provisionDiagnosis;
    @OneToOne
    private Employee admittingDoctor;
    private LocalDate admissionDate;
    private LocalDate expectedDischarge;
    private String specialComments;
    private String admissionNumber;

}

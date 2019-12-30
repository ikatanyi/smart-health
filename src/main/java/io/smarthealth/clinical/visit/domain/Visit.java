package io.smarthealth.clinical.visit.domain;

import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

/**
 * Patient CheckIn
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "patient_visit")
@Inheritance(strategy = InheritanceType.JOINED)
public class Visit extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_visit_patient_id"))
    private Patient patient;

    @NaturalId
    @Column(length = 38, unique = true)
    private String visitNumber;

    @ManyToOne(fetch = FetchType.LAZY/*, optional = false*/)
    @JoinColumn(name = "service_point_id", foreignKey = @ForeignKey(name = "fk_visit_service_point"))
    private ServicePoint servicePoint;

    private LocalDateTime startDatetime;
    private LocalDateTime stopDatetime;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private VisitEnum.VisitType visitType; //Outpatient | Hospitalization

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private VisitEnum.Status status;

    private Boolean scheduled;

}

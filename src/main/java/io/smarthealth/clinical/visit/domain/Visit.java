package io.smarthealth.clinical.visit.domain;

import io.smarthealth.accounting.doctors.domain.DoctorClinicItems;
import io.smarthealth.administration.servicepoint.domain.ServicePoint; 
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.time.LocalDateTime;
import javax.persistence.*; 
import lombok.Data;

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

    @Column(length = 38, unique = true, name = "visit_number")
    private String visitNumber;

    @ManyToOne(fetch = FetchType.LAZY/*, optional = false*/)
    @JoinColumn(name = "service_point_id", foreignKey = @ForeignKey(name = "fk_visit_service_point"))
    private ServicePoint servicePoint;

    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "health_provider", foreignKey = @ForeignKey(name = "fk_visit_health_provider"))
    private Employee healthProvider;

    private LocalDateTime startDatetime;
    private LocalDateTime stopDatetime;
    private String comments;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private VisitEnum.VisitType visitType; //Outpatient | Hospitalization

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private VisitEnum.Status status;

    private Boolean scheduled;

    @Enumerated(EnumType.STRING)
    private VisitEnum.PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private VisitEnum.ServiceType serviceType;

    private int triageCategory;
    
    private Boolean isActiveOnConsultation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_visit_clinic"))
    private DoctorClinicItems clinic;
    
    
}

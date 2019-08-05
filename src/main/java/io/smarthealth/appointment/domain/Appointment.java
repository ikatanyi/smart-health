package io.smarthealth.appointment.domain;

import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

/**
 * Patient Appointment
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "appointments")
public class Appointment extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @NaturalId
    private String appointmentNo;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_appointment_procedure_id"))
    private Item procedure;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_appointment_practioneer_id"))
    private Employee practioneer;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_appointment_department_id"))
    private Department department;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_appointment_type_id"))
    private AppointmentType appointmentType;

    private LocalDate appointmentDate;

    private LocalTime startTime;
    private LocalTime endTime;

    private Boolean allDay;
    private String urgency;
    private String status; //new followup 
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_appointment_referrer_id"))
    private Employee referredBy;
}

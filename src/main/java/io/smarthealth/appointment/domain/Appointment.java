package io.smarthealth.appointment.domain;

import io.smarthealth.common.domain.Auditable;
import io.smarthealth.organization.domain.Employee;
import io.smarthealth.patient.domain.Patient;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Patient Appointment
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "appointments")
public class Appointment extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private String reason;
    private Boolean allDay;
    private String visitType;
    @OneToOne
    private Employee appointmentWith;
    private String urgency;
    private String status; //new followup 

}

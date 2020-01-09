package io.smarthealth.appointment.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.facility.domain.Employee;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "appointment_type")
public class AppointmentType extends Identifiable{
    
    private String name;
    private Integer duration; // in minutes
    private String color; // calendar display color
    private String appointmentTypeNumber;
}

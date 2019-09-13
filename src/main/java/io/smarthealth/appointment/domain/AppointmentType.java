package io.smarthealth.appointment.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
    
}

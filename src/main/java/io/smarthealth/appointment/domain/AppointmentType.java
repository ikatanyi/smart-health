package io.smarthealth.appointment.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
public class AppointmentType extends Identifiable{
    private String name;
    private Integer duration; // in minutes
    private String color; // calendar display color
    
}

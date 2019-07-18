package io.smarthealth.appointment.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class AppointmentData implements Serializable{

    private String patientNumber;

    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private String reason;
    private Boolean allDay;
    private String visitType;
    private String appointmentWith;
    private String urgency;
    private String status; //new followup 
}

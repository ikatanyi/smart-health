package io.smarthealth.appointment.data;

import io.smarthealth.appointment.domain.Appointment;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class AppointmentData implements Serializable {

    public enum Status {
        Scheduled,
        Rescheduled,
        Confirmed,
        Checked_In,
        Done,
        Noshow,
        Cancelled
    }

    public enum Urgency {
        Urgent,
        Normal,
        Medical_Emergency
    }

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

    public static Appointment map(AppointmentData appointmentDto) {
        Appointment appointment = new Appointment();
        appointment.setAllDay(appointmentDto.getAllDay());
        appointment.setAppointmentDate(appointmentDto.appointmentDate); 
        appointment.setStatus(appointmentDto.getStatus());
        appointment.setUrgency(appointmentDto.getStatus()); 
        appointment.getPatient().setPatientNumber(appointmentDto.getPatientNumber());
        appointment.setEndTime(appointmentDto.getEndTime());
        appointment.setStartTime(appointmentDto.getStartTime());
        return appointment;
    }

    public static AppointmentData map(Appointment appointmentEntity) {
        AppointmentData ap = new AppointmentData();
        ap.setAllDay(appointmentEntity.getAllDay());
        ap.setAppointmentDate(appointmentEntity.getAppointmentDate()); 
        ap.setEndTime(appointmentEntity.getEndTime());
        ap.setStartTime(appointmentEntity.getStartTime());
        ap.setPatientNumber(appointmentEntity.getPatient().getPatientNumber()); 
        ap.setStatus(appointmentEntity.getStatus());
        ap.setUrgency(appointmentEntity.getUrgency()); 
        return ap;
    }
}

package io.smarthealth.appointment.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.appointment.domain.Appointment;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.modelmapper.ModelMapper;

/**
 *
 * @author Kelsas
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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
    private Long practionerId;
    private Long departmentId;
    @NotNull
    @NotBlank
    private String typeOfAppointment;
    private Long appointmentTypeId;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean allDay;
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(dataType = "string", allowableValues = "Urgent, Normal, Medical_Emergency", value = "Urgency", notes = "Urgency level")
//    @ApiOperation(value = "Brief description of your operation.", response = Urgency.class)
    private Urgency urgency;
    @Enumerated(EnumType.STRING)
    private Status status; //new followup  
    private Long referredBy;

    public static Appointment map(AppointmentData data) {
        ModelMapper mapper = new ModelMapper();
        Appointment appointment = mapper.map(data, Appointment.class);
        return appointment;
    }

    public static AppointmentData map(Appointment appointment) {
        ModelMapper mapper = new ModelMapper();
        AppointmentData data = mapper.map(appointment, AppointmentData.class);
        return data;
    }
}

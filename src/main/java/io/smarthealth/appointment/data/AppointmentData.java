package io.smarthealth.appointment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.appointment.domain.Appointment;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import static io.smarthealth.infrastructure.lang.Constants.TIME_PATTERN;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    private String practitionerCode;
    private String typeOfAppointment;
    private Long appointmentTypeId;
    private String appointmentNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate appointmentDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_PATTERN)
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_PATTERN)
    private LocalTime endTime;
    private Boolean allDay;
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(dataType = "string", allowableValues = "Urgent, Normal, Medical_Emergency", value = "Urgency", notes = "Urgency level")
//    @ApiOperation(value = "Brief description of your operation.", response = Urgency.class)
    private Urgency urgency;
    @Enumerated(EnumType.STRING)
    private Status status; //new followup  
    private String comments;

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

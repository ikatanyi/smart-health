package io.smarthealth.appointment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.appointment.domain.Appointment;
import io.smarthealth.appointment.domain.enumeration.StatusType;
import io.smarthealth.appointment.domain.enumeration.Urgency;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import static io.smarthealth.infrastructure.lang.Constants.TIME_PATTERN;
import io.smarthealth.organization.facility.data.EmployeeData;
import io.smarthealth.organization.facility.domain.Employee.Category;
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

    @ApiModelProperty(required = false, hidden = false)
    private Long appointmentId;
    private String patientNumber;
    private String patientName;
    private String practitionerCode;
    @Enumerated(EnumType.STRING)
    private Category practitionerCategory;
    private String practionerName;
    private String departmentName;
    private String typeOfAppointment;
    private Long appointmentTypeId;
    private String appointmentTypeName;
    private String appointmentNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate appointmentDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_PATTERN)
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_PATTERN)
    private LocalTime endTime;
    private Boolean allDay;
    @Enumerated(EnumType.STRING)
//    @ApiModelProperty(dataType = "string", allowableValues = "Urgent, Normal, Medical_Emergency", value = "Urgency", notes = "Urgency level")
//    @ApiOperation(value = "Brief description of your operation.", response = Urgency.class)
    private Urgency urgency;
    private StatusType status; //new followup  
    private String comments;
    
    private String firstName;
    private String LastName;
    private String gender;
    private String phoneNumber;
    
    @ApiModelProperty(required = false, hidden = true)
    private EmployeeData practitionerData;

    public static Appointment map(AppointmentData data) {
        ModelMapper mapper = new ModelMapper();
        Appointment appointment = mapper.map(data, Appointment.class);
        return appointment;
    }

    public static AppointmentData map(Appointment appointment) {
        AppointmentData data = new AppointmentData();//mapper.map(appointment, AppointmentData.class);
        if (appointment.getAppointmentType() != null) {
            data.setAppointmentTypeId(appointment.getAppointmentType().getId());
            data.setAppointmentTypeName(appointment.getAppointmentType().getName());
        }
        if (appointment.getPatient() != null) {
            data.setPatientNumber(appointment.getPatient().getPatientNumber());
            data.setPatientName(appointment.getPatient().getFullName());
        }
        
        if (appointment.getPractitioner() != null) {
            data.setPractitionerCode(appointment.getPractitioner().getStaffNumber());
            data.setPractionerName(appointment.getPractitioner().getFullName());
            data.setDepartmentName(appointment.getPractitioner().getDepartment().getName());
            data.setPractitionerCategory(appointment.getPractitioner().getEmployeeCategory());
        }
        data.setFirstName(appointment.getFirstName());
        data.setLastName(appointment.getLastName());
        data.setGender(appointment.getGender());
        data.setAppointmentId(appointment.getId());
        data.setAppointmentDate(appointment.getAppointmentDate());
        data.setAppointmentNo(appointment.getAppointmentNo());
        data.setComments(appointment.getComments());
        data.setEndTime(appointment.getEndTime());
        data.setStartTime(appointment.getStartTime());
        data.setStatus(appointment.getStatus());
        data.setUrgency(appointment.getUrgency());
        data.setPhoneNumber(appointment.getPhoneNumber());
        return data;
    }
}

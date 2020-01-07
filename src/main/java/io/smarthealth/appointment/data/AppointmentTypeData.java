/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.appointment.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.appointment.domain.Appointment;
import io.smarthealth.appointment.domain.AppointmentType;
import io.smarthealth.organization.facility.data.EmployeeData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AppointmentTypeData {

    @ApiModelProperty(required = false, hidden = true)
    private Long id;
    

   // @NotNull
    private String name;
    //@NotNull
    private Integer duration; // in minutes
    private String color; // calendar display color
    private String staffNumber;
    private String appointmentTypeNumber;
    
    
    public static AppointmentType map(AppointmentTypeData data) {
        AppointmentType appointmentType = new AppointmentType();//mapper.map(appointment, AppointmentData.class);
        appointmentType.setId(data.getId());
        appointmentType.setName(data.getName());
        appointmentType.setColor(data.getColor());
        appointmentType.setDuration(data.getDuration());
        return appointmentType;
    }
    
    
}

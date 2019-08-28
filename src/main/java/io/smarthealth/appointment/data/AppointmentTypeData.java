/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.appointment.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
}

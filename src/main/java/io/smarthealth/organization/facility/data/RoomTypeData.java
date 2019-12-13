/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.data;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class RoomTypeData {

    @NotNull
    private String name;
    @NotNull
    private String typeCode;
    @NotNull
    private double chargesPerNight;
    private String specialComments;
    
}

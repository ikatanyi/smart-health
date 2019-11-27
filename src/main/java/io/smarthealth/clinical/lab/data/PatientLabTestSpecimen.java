/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class PatientLabTestSpecimen {

    private Long specimenId;
    @ApiModelProperty(required = false, hidden = true)
    private String specimenName;
}

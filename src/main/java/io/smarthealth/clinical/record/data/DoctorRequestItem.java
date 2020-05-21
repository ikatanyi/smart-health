/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class DoctorRequestItem {

    private Long itemId;
    private String itemName;
    private String code;
    private double costRate;
    private double rate;
    private Long requestItemId;
    @ApiModelProperty(hidden = true)
    private PrescriptionData prescriptionData;

    private String orderNo;
    private String requestedByName, requestedByNo;

}

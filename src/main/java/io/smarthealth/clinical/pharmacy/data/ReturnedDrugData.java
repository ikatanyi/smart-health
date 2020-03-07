/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.pharmacy.data;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ReturnedDrugData implements Serializable {
    @ApiModelProperty(required=false, hidden=true)
    private Long id;
    private Long drugId;
    @ApiModelProperty(required=false, hidden=true)
    private String drug;
    private Double quantity;
    private String reason;
}

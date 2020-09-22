/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.data;

import io.smarthealth.clinical.admission.domain.BedCharge;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kennedy.ikatanyi
 */
@Data
public class BedChargeData {
    
    @ApiModelProperty(hidden = true)
    private Long id;
//    private Long bedTypeId;
    private Long itemId;
    @ApiModelProperty(hidden = true)
    private String itemCode;
    @ApiModelProperty(hidden = true)
    private String item;
    private BigDecimal rate;
    private Boolean active;
    private Boolean recurrent;
    
    @ApiModelProperty(hidden = true)
    private String bedType;

    public BedCharge map(){
        BedCharge charge = new BedCharge();
        charge.setActive(this.getActive());
        charge.setRate(this.getRate());
        return charge;
    }
}

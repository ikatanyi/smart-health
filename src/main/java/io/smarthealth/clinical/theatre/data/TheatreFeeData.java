/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.theatre.data;

import io.smarthealth.clinical.theatre.domain.enumeration.FeeCategory;
import io.smarthealth.clinical.theatre.domain.TheatreFee;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class TheatreFeeData {

    private Long id;
    private boolean isPercentage;
    private BigDecimal value;
    private Long itemId;
    private String itemCode;
    private String itemName;
    private FeeCategory feeCategory; 

    public static TheatreFeeData map(TheatreFee configuration) {
        TheatreFeeData data = new TheatreFeeData();
        data.setId(configuration.getId());
        data.setPercentage(configuration.getIsPercentage());
        data.setValue(configuration.getAmount());
        data.setItemId(configuration.getServiceType().getId());
        data.setItemCode(configuration.getServiceType().getItemCode());
        data.setItemName(configuration.getServiceType().getItemName());
        data.setFeeCategory(configuration.getFeeCategory());
        return data;
    }
}

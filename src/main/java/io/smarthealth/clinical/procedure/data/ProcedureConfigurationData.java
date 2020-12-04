/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.smarthealth.clinical.procedure.domain.enumeration.FeeCategory;
import io.smarthealth.clinical.procedure.domain.ProcedureConfiguration;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ProcedureConfigurationData {

    private Long id;
    private boolean isPercentage;
    private BigDecimal value;
    private Long itemId;
    private String itemCode;
    private String itemName;
    private FeeCategory feeCategory;
    @JsonIgnore
    private BigDecimal actualAmount;

    public static ProcedureConfigurationData map(ProcedureConfiguration configuration) {
        ProcedureConfigurationData data = new ProcedureConfigurationData();
        data.setId(configuration.getId());
        data.setPercentage(configuration.isPercentage());
        data.setValue(configuration.getValueAmount());
        data.setItemId(configuration.getProcedure().getId());
        data.setItemCode(configuration.getProcedure().getItemCode());
        data.setItemName(configuration.getProcedure().getItemName());
        data.setFeeCategory(configuration.getFeeCategory());
        return data;
    }
}

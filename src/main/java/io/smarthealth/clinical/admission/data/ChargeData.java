package io.smarthealth.clinical.admission.data;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Data
public class ChargeData {
    private Long bedChargeId;    
    @ApiModelProperty(hidden=true)
    private BigDecimal rate;
    @ApiModelProperty(hidden=true)
    private Boolean recurrentCost;
    @ApiModelProperty(hidden=true)
    private String name;
    @ApiModelProperty(hidden=true)
    private String itemCode;
}

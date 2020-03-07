package io.smarthealth.debtor.claim.dispatch.data;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class DispatchedInvoiceData {    
    
    private String invoiceNumber;
    @ApiModelProperty(required=false,hidden=true)
    private String payerName;
    @ApiModelProperty(required=false,hidden=true)
    private String schemeName;
    @ApiModelProperty(required=false,hidden=true)
    private Double invoiceAmount;
    @ApiModelProperty(required=false,hidden=true)
    private LocalDate dueDate;
}

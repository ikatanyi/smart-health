package io.smarthealth.accounting.payment.data;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class SupplierPaymentData {
    private LocalDate invoiceDate;
    private String invoiceNumber;
    @ApiModelProperty(hidden=true, required=false)
    private String description;
    private BigDecimal invoiceAmount;
    private BigDecimal amountPaid;
    private BigDecimal taxAmount;
}

package io.smarthealth.accounting.payment.data;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptItemData {
  
    private Long id;
    private String receiptNumber;
    private String itemName;
    private String itemCode;
    private Double quantity;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal taxes;
    private BigDecimal amountPaid;  
    @ApiModelProperty(required=false, hidden=true)
    private String servicePoint;
    @ApiModelProperty(required=false, hidden=true)
    private String servicePointId;
    @ApiModelProperty(required=false, hidden=true)
    private String patientName;
    @ApiModelProperty(required=false, hidden=true)
    private String referenceNumber;
    @ApiModelProperty(required=false, hidden=true)
    private LocalDate transactionDate;
    @ApiModelProperty(required=false, hidden=true)
    private String paymentMode;
}

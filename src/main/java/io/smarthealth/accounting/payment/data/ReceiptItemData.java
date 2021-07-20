package io.smarthealth.accounting.payment.data;

import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
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
    private ItemCategory itemCategory;
    private Double quantity;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal taxes;
    private BigDecimal amountPaid;
    private String servicePoint;
    private String servicePointId;
    private String patientName;
    private String referenceNumber;
    private LocalDate transactionDate;
    private String paymentMode;
}

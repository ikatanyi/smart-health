package io.smarthealth.accounting.payment.data;

import java.math.BigDecimal;
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
    
    
}

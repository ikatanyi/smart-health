package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    private String ReceiptNumber;
    private String itemName;
    private String itemCode;
    private Double quantity;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal taxes;
    private BigDecimal amountPaid;  
    
    
}

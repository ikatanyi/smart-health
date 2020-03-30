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
public class ReceiptData {
  
    private Long id;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate transactionDate;
    private String payer;
    private String description; //Insurance payment | Cheque deposit
    private BigDecimal amount;
//    private BigDecimal credit;
    private BigDecimal refundedAmount;
    private String paymentMethod;
    private String receiptNo;
    private String referenceNumber; //voucher no,
    private String transactionType;
    private String transactionNo;
    private String shiftNo;
    private String currency;
    private String createdBy;
    private List<ReceiptTransactionData> transactions = new ArrayList<>();
    
    
}

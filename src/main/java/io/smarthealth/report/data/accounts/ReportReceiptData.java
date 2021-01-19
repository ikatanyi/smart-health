package io.smarthealth.report.data.accounts;

import io.smarthealth.accounting.cashier.domain.ShiftStatus;
import io.smarthealth.accounting.payment.data.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.payment.domain.ReceiptItem;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class ReportReceiptData {
  
    private Long id;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDateTime transactionDate;
    private String payer;
    private String payerId;
    private String description; //Insurance payment | Cheque deposit
    private BigDecimal amount = BigDecimal.ZERO;;
    private BigDecimal paid;
    private BigDecimal tenderedAmount;
    private BigDecimal refundedAmount;
    private String paymentMethod;
    private String receiptNo;
    private String referenceNumber; //voucher no,
    private String transactionType;
    private String transactionNo;
    private String shiftNo;
    private String cashier;
    private LocalDateTime startDate;
    private LocalDateTime stopDate;
    private ShiftStatus status;
    private String currency;
    private String createdBy;
    
    private BigDecimal lab = BigDecimal.ZERO;
    private BigDecimal pharmacy = BigDecimal.ZERO;
    private BigDecimal radiology = BigDecimal.ZERO;
    private BigDecimal consultation = BigDecimal.ZERO;
    private BigDecimal procedure = BigDecimal.ZERO;
    private BigDecimal copayment = BigDecimal.ZERO;
    private BigDecimal other = BigDecimal.ZERO;
    
    private BigDecimal mobilemoney= BigDecimal.ZERO;
    private BigDecimal cash = BigDecimal.ZERO;
    private BigDecimal card = BigDecimal.ZERO;
    private BigDecimal bank = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal otherPayment = BigDecimal.ZERO;
    
    
}

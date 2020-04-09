package io.smarthealth.accounting.payment.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class RemittanceData {

    private Long id;
    private Long payerId;
    private String payer;
    private String receiptNo;
    private String description; //Insurance payment | Cheque deposit
    private BigDecimal amount;
    private String paymentMethod;
    private String referenceNumber; //voucher no, 
    private LocalDateTime remittanceDate;
    private String transactionNo;
    private String currency;
    private String remittanceNo;
    private BigDecimal balance;

}

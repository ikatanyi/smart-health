package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.invoice.data.CreateInvoiceItemData;
import io.smarthealth.accounting.payment.domain.enumeration.PaymentStatus;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class CreateTransactionData {

    private Long id;

    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime date;

    private String shiftNo;
 
    private String payee;
    
    private String payeeName;
    
    private String billNumber;
    
    private String currency;

    private List<CreateInvoiceItemData> billItems = new ArrayList<>();

    private List<PaymentData> payment = new ArrayList<>();

    private PaymentStatus status;

    private Double amount;
    
    private String transactionId;

}

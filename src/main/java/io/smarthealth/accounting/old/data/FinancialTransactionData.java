package io.smarthealth.accounting.old.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.old.domain.FinancialTransaction;
import io.smarthealth.accounting.payment.domain.enumeration.TrnxType;
import io.smarthealth.accounting.payment.domain.enumeration.PaymentStatus;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class FinancialTransactionData {
    private Long id;
    
  @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime date;
 
    @Enumerated(EnumType.STRING)
    private TrnxType trxType;

    private String receiptNo;
    
    private String shiftNo;
    
    private String transactionId; 
     
    private String account;
    
    private String accountName;
 
    private String invoice;
   
    private Long parentTransactionId;
    
    private List<PaymentoldData> payment = new ArrayList<>();
      
    private PaymentStatus status;
     
    private Double amount;  
}

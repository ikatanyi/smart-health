package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.payment.domain.FinancialTransaction;
import io.smarthealth.accounting.payment.domain.enumeration.TrxType;
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
    private TrxType trxType;

    private String receiptNo;
    
    private String shiftNo;
    
    private String transactionId; 
     
    private String account;
    
    private String accountName;
 
    private String invoice;
   
    private Long parentTransactionId;
    
    private List<PaymentData> payment = new ArrayList<>();
      
    private PaymentStatus status;
     
    private Double amount; 
    
    public static FinancialTransactionData map(FinancialTransaction transaction) {
        FinancialTransactionData data = new FinancialTransactionData();
        data.setId((transaction.getId())); 
        data.setDate(transaction.getDate());
        data.setTrxType(transaction.getTrxType());
        data.setReceiptNo(transaction.getReceiptNo());
        data.setShiftNo(transaction.getShiftNo());
        data.setTransactionId(transaction.getTransactionId());
        data.setInvoice(transaction.getInvoice());
       data.setAmount(transaction.getAmount());
        if (transaction.getParentTransaction() != null) {
            data.setParentTransactionId(transaction.getParentTransaction().getId());
        }
        
        if(transaction.getAccount()!=null){
            data.setAccount(transaction.getAccount().getName());
            data.setAccountName(transaction.getAccount().getName());
        }
         
        if (!transaction.getPayments().isEmpty()) {
            data.setPayment(
                    transaction.getPayments()
                            .stream()
                            .map(p -> PaymentData.map(p))
                            .collect(Collectors.toList())
            );
        }

        data.setStatus(transaction.getStatus());
       
        return data;
    }
}

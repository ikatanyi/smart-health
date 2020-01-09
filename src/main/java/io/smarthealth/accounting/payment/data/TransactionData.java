package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.payment.domain.Transaction;
import io.smarthealth.accounting.payment.domain.TranxType;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class TransactionData {

    private Long id;
    private String payer;
    private String invoice;
    private String creditNote;
    private String receiptNo;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime date;
    private TranxType type;
    private Long parentTransaction;
    private String method;
    private String status;// succeeded, pending,failed
    private String currency;
    private Double amount;
    private String notes;
     private String shiftNo;

    public static TransactionData map(Transaction transaction) {
        TransactionData data = new TransactionData();
        data.setId((transaction.getId()));
        data.setPayer(transaction.getPayer());
        data.setInvoice(transaction.getInvoice());
        data.setCreditNote(transaction.getCreditNote());
        data.setDate(transaction.getDate());
        data.setType(transaction.getType());
        if (transaction.getParentTransaction() != null) {
            data.setParentTransaction(transaction.getParentTransaction().getId());
        }
        data.setMethod(transaction.getMethod());
        data.setStatus(transaction.getStatus());
        data.setCurrency(transaction.getCurrency());
        data.setAmount(transaction.getAmount());
        data.setNotes(transaction.getNotes());
        data.setShiftNo(transaction.getShiftNo());
        return data;
    }
}

package io.smarthealth.debtor.claim.allocation.data;

import io.smarthealth.debtor.claim.allocation.domain.*;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class AllocationData {  
    private String invoiceNo;
    private Long remitanceId;
    private Double invoiceAmount;
    private Double amount;
    private Double balance;
    private String remittanceNo;
    private String transactionId;
    private String receiptNo;   
    
    public static AllocationData map(Allocation allocation){
        AllocationData data = new AllocationData();
        data.setAmount(allocation.getAmount());
        data.setBalance(allocation.getBalance());
        if(allocation.getInvoice()!=null){
            data.setInvoiceAmount(allocation.getInvoice().getBill().getAmount());
            data.setInvoiceNo(allocation.getInvoice().getNumber());
        }
        
        data.setReceiptNo(allocation.getReceiptNo());
        data.setRemittanceNo(allocation.getRemittanceNo());
        data.setTransactionId(allocation.getTransactionId());
        return data;
    }
    
    public static Allocation map(AllocationData data){
        Allocation allocation = new Allocation();
        allocation.setAmount(data.getAmount());
        allocation.setBalance(data.getBalance());        
        allocation.setReceiptNo(data.getReceiptNo());
        allocation.setRemittanceNo(data.getRemittanceNo());
        allocation.setTransactionId(data.getTransactionId());
        return allocation;
    }
}

package io.smarthealth.debtor.claim.allocation.data;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import io.smarthealth.debtor.claim.allocation.domain.*;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class AllocationData {

    private String invoiceNo;
    private BigDecimal amount;
    @ApiModelProperty(hidden = true)
    private Long remitanceId;
    @ApiModelProperty(hidden = true)
    private BigDecimal invoiceAmount;
    @ApiModelProperty(hidden = true)
    private BigDecimal balance;
    @ApiModelProperty(hidden = true)
    private String remittanceNo;
    @ApiModelProperty(hidden = true)
    private String transactionId;
    @ApiModelProperty(hidden = true)
    private String receiptNo;

  

    public static AllocationData map(Allocation allocation) {
        AllocationData data = new AllocationData();
        data.setAmount(allocation.getAmount());
        data.setBalance(allocation.getBalance());
        if (allocation.getInvoice() != null) {
            data.setInvoiceAmount(allocation.getInvoice().getAmount());
            data.setInvoiceNo(allocation.getInvoice().getNumber());
        }

        data.setReceiptNo(allocation.getReceiptNo());
        data.setRemittanceNo(allocation.getRemittanceNo());
        data.setTransactionId(allocation.getTransactionId());
        data.setRemitanceId(allocation.getId());
        data.setBalance(allocation.getBalance());
        return data;
    }

    public static Allocation map(AllocationData data) {
        Allocation allocation = new Allocation();
        allocation.setAmount(data.getAmount());
        allocation.setBalance(data.getBalance());
        allocation.setReceiptNo(data.getReceiptNo());
        allocation.setRemittanceNo(data.getRemittanceNo());
        allocation.setTransactionId(data.getTransactionId());
        return allocation;
    }
}

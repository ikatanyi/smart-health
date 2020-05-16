package io.smarthealth.debtor.claim.allocation.data;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import io.smarthealth.debtor.claim.allocation.domain.*;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class AllocationData {

    private String invoiceNo;
    private String payer;
    private String scheme;
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
    @ApiModelProperty(hidden = true)
    private LocalDate invoiceDate;
    @ApiModelProperty(hidden = true)
    private LocalDate transactionDate;
    

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

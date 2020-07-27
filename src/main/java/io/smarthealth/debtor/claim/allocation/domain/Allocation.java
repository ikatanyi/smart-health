package io.smarthealth.debtor.claim.allocation.domain;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.debtor.claim.allocation.data.AllocationData;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.infrastructure.utility.RoundingHelper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
@Table(name = "patient_invoice_allocation")
public class Allocation extends Auditable {

    @ManyToOne
    private Invoice invoice;
    private BigDecimal amount;
    private BigDecimal balance;
    private String remittanceNo;
    private String transactionId;
    private String receiptNo;
    private String comments;
    private LocalDate transactionDate;
    
    public AllocationData map() {
        AllocationData data = new AllocationData();
        data.setAmount(this.getAmount());
        data.setBalance(this.getBalance());
        if (this.getInvoice() != null) {
            data.setInvoiceAmount(this.getInvoice().getAmount().setScale(2,RoundingMode.HALF_UP));
            data.setInvoiceNo(this.getInvoice().getNumber());
            data.setPayer(this.getInvoice().getPayer().getPayerName());
            data.setScheme(this.getInvoice().getScheme().getSchemeName());
            data.setInvoiceDate(this.getInvoice().getDate());
        }
        data.setTransactionDate(LocalDate.from(this.getCreatedOn().atZone(ZoneId.systemDefault())));
        data.setReceiptNo(this.getReceiptNo());
        data.setRemittanceNo(this.getRemittanceNo());
        data.setTransactionId(this.getTransactionId());
        data.setRemitanceId(this.getId());
        data.setBalance(this.getBalance());
        data.setComments(this.getComments());
        return data;
    }
}

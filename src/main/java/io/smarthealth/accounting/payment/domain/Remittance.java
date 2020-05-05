package io.smarthealth.accounting.payment.domain;

import io.smarthealth.accounting.payment.data.RemittanceData;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "acc_remittance") 
public class Remittance extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_remittance_payer_id"))
    private Payer payer;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_remittance_receipt_id"))
    private Receipt receipt;
    private String remittanceNo;
    private LocalDateTime remittanceDate;
    private BigDecimal balance;

    public Remittance() {
    }

    public Remittance(Payer payer, Receipt receipt) {
        this.payer = payer;
        this.receipt = receipt;
        this.remittanceNo = receipt.getReferenceNumber();
        this.balance = receipt.getAmount();
        this.remittanceDate = receipt.getTransactionDate();
    }

    public Remittance(Payer payer, Receipt receipt, String remittanceNo) {
        this.payer = payer;
        this.receipt = receipt;
        this.remittanceNo = remittanceNo;
        this.balance = receipt.getAmount();
        this.remittanceDate = receipt.getTransactionDate();
    }

    public RemittanceData toData() {
        RemittanceData data = new RemittanceData();
        data.setId(this.getId());
        if (this.payer != null) {
            data.setPayer(this.payer.getPayerName());
            data.setPayerId(this.payer.getId());
        }
        data.setBalance(this.balance);
        data.setRemittanceDate(this.remittanceDate);
        if (this.receipt != null) {
            data.setReceiptNo(this.receipt.getReceiptNo());
            data.setDescription(this.receipt.getDescription());
            data.setAmount(this.receipt.getAmount());
            data.setPaymentMethod(this.receipt.getPaymentMethod());
            data.setReferenceNumber(this.receipt.getReferenceNumber());
            data.setTransactionNo(this.receipt.getTransactionNo());
            data.setCurrency(this.receipt.getCurrency());
        }
        data.setRemittanceNo(this.remittanceNo);
        return data;
    }
}

package io.smarthealth.accounting.payment.domain;

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
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_remittance_payment_id"))
    private Receipt payment;
    private String remittanceNo;
    private LocalDateTime remittanceDate;
    private BigDecimal balance;

    public Remittance() {
    }

    public Remittance(Payer payer, Receipt payment) {
        this.payer = payer;
        this.payment = payment;
        this.remittanceNo = payment.getReferenceNumber();
        this.balance = payment.getAmount();
        this.remittanceDate = payment.getTransactionDate();
    }

    public Remittance(Payer payer, Receipt payment, String remittanceNo) {
        this.payer = payer;
        this.payment = payment;
        this.remittanceNo = remittanceNo;
        this.balance = payment.getAmount();
        this.remittanceDate = payment.getTransactionDate();
    }

}

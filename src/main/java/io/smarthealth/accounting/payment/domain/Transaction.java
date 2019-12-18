package io.smarthealth.accounting.payment.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "payment_transaction")
public class Transaction extends Auditable {

    private String payer;
    private String invoice;
    private String creditNote;
     private String receiptNo;
    @Column(name = "transaction_date")
    private LocalDateTime date;
    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TranxType type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_transaction", foreignKey = @ForeignKey(name = "fk_payment_transaction_tranx_id"))
    private Transaction parentTransaction;
    private String method;
    private String status;// succeeded, pending,failed
    private String currency;
    private Double amount;
    private String notes;
    private String shiftNo;

}

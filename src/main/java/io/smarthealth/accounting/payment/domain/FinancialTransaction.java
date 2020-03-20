package io.smarthealth.accounting.payment.domain;

import io.smarthealth.accounting.payment.domain.enumeration.TrxType;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.payment.data.FinancialTransactionData;
import io.smarthealth.accounting.payment.domain.enumeration.PaymentStatus;
import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "financial_transaction")
public class FinancialTransaction extends Auditable {

    @Column(name = "transaction_date")
    private LocalDateTime date;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TrxType trxType;

    private String invoice;// bill number for this particular transaction

    private String receiptNo;

    private String shiftNo;

    private String transactionId;

    private Double amount;
    
    private Boolean isRefund;
    
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payment_trx_account_id"))
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_transaction", foreignKey = @ForeignKey(name = "fk_payment_trx_parent_trx_id"))
    private FinancialTransaction parentTransaction;
    
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public void addPayment(Payment payment) {
        payment.setTransaction(this);
        payments.add(payment);
    }

    public void addPayments(List<Payment> payments) {
        this.payments = payments;
        this.payments.forEach(x -> x.setTransaction(this));
    }
    
    public  FinancialTransactionData toData() {
        FinancialTransactionData data = new FinancialTransactionData();
        data.setId(this.getId()); 
        data.setDate(this.date);
        data.setTrxType(this.trxType);
        data.setReceiptNo(this.receiptNo);
        data.setShiftNo(this.shiftNo);
        data.setTransactionId(this.transactionId);
        data.setInvoice(this.invoice);
       data.setAmount(this.amount);
        if (this.parentTransaction != null) {
            data.setParentTransactionId(this.parentTransaction.getId());
        }
        
        if(this.account!=null){
            data.setAccount(this.account.getName());
            data.setAccountName(this.account.getName());
        }
         
        if (!this.payments.isEmpty()) {
            data.setPayment(
                    this.payments
                            .stream()
                            .map(p -> p.toData())
                            .collect(Collectors.toList())
            );
        }

        data.setStatus(this.status);
       
        return data;
    }
}

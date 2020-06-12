package io.smarthealth.accounting.payment.domain;

import io.smarthealth.accounting.payment.data.BankingData;
import io.smarthealth.accounting.payment.domain.enumeration.BankingType;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.bank.domain.BankAccount;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "acc_banking")
public class Banking extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_bankinf_bank_account_id"))
    private BankAccount bankAccount;

    @Column(name = "transaction_date")
    private LocalDate date;
    private String client;
    private String description; //Insurance payment | Cheque deposit
    private BigDecimal debit;
    private BigDecimal credit;
    private String paymentMode;
    private String referenceNumber; //voucher no,
    @Enumerated(EnumType.STRING)
    private BankingType transactionType;
    private String transactionNo;
    private String currency;

    public BankingData toData() {
        BankingData data = new BankingData();
        data.setId(this.getId());
        if (this.bankAccount != null) {
            data.setBankAccountName(this.bankAccount.getAccountName());
            data.setBankAccountNumber(this.bankAccount.getAccountNumber());
        }
        data.setClient(this.client);
        data.setDescription(this.description);
        data.setDebit(this.debit);
        data.setCredit(this.credit);
        data.setPaymentMode(this.paymentMode);
        data.setReferenceNumber(this.referenceNumber);
        data.setTransactionType(this.transactionType);
        data.setTransactionNo(this.transactionNo);
        data.setTransactionDate(this.date);
        data.setCurrency(this.currency);
        return data;
    }

    public static Banking deposit(BankAccount bankAccount, Receipt receipt, BigDecimal amount) {
        return deposit(bankAccount, receipt.getPayer(), receipt.getDescription(), amount, receipt.getPaymentMethod(), receipt.getReferenceNumber(), receipt.getTransactionNo(), receipt.getCurrency());

    }

    public static Banking deposit(BankAccount bankAccount, String client, String description, BigDecimal amount, String paymentMode, String referenceNumber, String transactionNo, String currency) {
        return new Banking(bankAccount, LocalDate.now(), client, description, amount, BigDecimal.ZERO, paymentMode, referenceNumber, BankingType.Banking, transactionNo, currency);

    }

}

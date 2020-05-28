package io.smarthealth.accounting.accounts.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.smarthealth.accounting.accounts.data.JournalEntryItemData;
import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
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
@Data
@Entity
@Table(name = "acc_journal_entry_items")
public class JournalEntryItem extends Auditable {
 
    @JsonIgnore
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_journal_entry_items_journal_id"))
    private JournalEntry journalEntry;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_journal_entry_items_account_id"))
    private Account account;
    private String description;
    private BigDecimal debit;
    private BigDecimal credit;
//    @Enumerated(EnumType.STRING)
//    private TransactionType transactionType;

//    public static enum Type {
//        DEBIT, CREDIT
//    }
    //TODO:: I thought we needed transaction type here :) 
//    @Enumerated(EnumType.STRING)
//    private TransactionType transactionType;
//    private String accountNumber;
//    @Enumerated(EnumType.STRING)
//    private Type type;
//    private BigDecimal amount;
    protected JournalEntryItem() {
    }

    public JournalEntryItem(Account account, String description, BigDecimal debit, BigDecimal credit) {
        this.account = account;
        this.description = description;
        this.debit = debit;
        this.credit = credit;
//          this.transactionType =journalEntry.getTransactionType();
    }

//    public JournalEntryItem(Account account, String description, BigDecimal debit, BigDecimal credit, TransactionType transactionType) {
//        this.account = account;
//        this.description = description;
//        this.debit = debit;
//        this.credit = credit;
//        this.transactionType = transactionType;
//    }
//    public JournalEntryItem(String description, String accountNumber, Type type, BigDecimal amount) {
//        Validate.notNull(accountNumber, "Account Number is required");
//        Validate.notNull(type, "Journal Entry Type is required");
//        Validate.notNull(amount, "Amount is required");
//        this.description = description;
//        this.accountNumber = accountNumber;
//        this.type = type;
//        this.amount = amount;
//    }
    public boolean isCredit() {
        return this.credit != BigDecimal.ZERO;
    }

    public boolean isDebit() {
        return this.debit != BigDecimal.ZERO;
    }

    public JournalEntryItemData toData() {
        JournalEntryItemData data = new JournalEntryItemData();
        data.setId(this.getId());
        data.setDate(this.journalEntry.getDate());
        data.setAccountName(this.account.getName());
        data.setAccountNumber(this.account.getIdentifier());
        data.setCredit(this.credit);
        data.setDebit(this.debit);
        data.setFormattedDebit(
                NumberFormat.getCurrencyInstance(new Locale("en", "KE")).format(this.debit)
        );
        data.setFormattedCredit(
                NumberFormat.getCurrencyInstance(new Locale("en", "KE")).format(this.credit)
        );
        data.setDescription(this.description);
        data.setJournalId(this.journalEntry.getId());
        data.setStatus(this.journalEntry.getStatus());
        data.setTransactionNo(this.journalEntry.getTransactionNo());
        data.setCreatedBy(this.getCreatedBy());
        data.setType(this.journalEntry.getTransactionType());
        return data;
    }
    
      @Override
    public String toString() {
        return "Journal Entry Item [id=" + getId() + ", Journal =" + journalEntry.getDescription() + ", account=" + account!=null ? account.getName(): null + ", debit=" + debit + ", credit=" +credit+ " ]";
    }
}

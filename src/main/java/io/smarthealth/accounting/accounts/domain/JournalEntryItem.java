package io.smarthealth.accounting.accounts.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import org.apache.commons.lang3.Validate;

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
    private JournalEntry journalEntry;

    public static enum Type {
        DEBIT, CREDIT
    }
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    private Type type;
    private BigDecimal amount;

    protected JournalEntryItem() {
    }

    public JournalEntryItem(String accountNumber, Type type, BigDecimal amount) {
        Validate.notNull(accountNumber, "Account Number is required");
        Validate.notNull(type, "Journal Entry Type is required");
        Validate.notNull(amount, "Amount is required");
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;

    }

    public boolean isCredit() {
        return this.type == Type.CREDIT;
    }

    public boolean isDebit() {
        return this.type == Type.DEBIT;
    }
}

package io.smarthealth.accounting.account.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "account_journal_entry")
public class JournalEntry extends Auditable {

    @ManyToOne
    @JoinColumn(name = "journal_id")
    private Journal journal;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    private Double credit;
    private Double debit;
    private String description;//optional comments about the entry
    private LocalDate entryDate;
    private boolean isBalanceCalculated = false;
    private BigDecimal runningBalance;

    public JournalEntry() {
        super();
    }

    public JournalEntry(Account account, Double credit, Double debit, LocalDate entryDate, String description) {
        this.account = account;
        this.credit = credit;
        this.debit = debit;
        this.entryDate = entryDate;
        this.description=description;
    }

    public boolean isDebit() {
        return debit > 0;
    }

    public BigDecimal getAmount() {
        if (isDebit()) {
            return BigDecimal.valueOf(debit);
        }
        return BigDecimal.valueOf(credit);
    }
}

package io.smarthealth.accounting.accounts.domain;

import io.smarthealth.accounting.accounts.domain.JournalEntryItem.Type;
import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "acc_journal_entry")
public class JournalEntry extends Auditable {

    @Column(name = "transaction_date")
    private LocalDate date;
    private BigDecimal amount;
    private String transactionNo;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private String description;
    @Enumerated(EnumType.STRING)
    private JournalState status;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL)
    private List<JournalEntryItem> items = new ArrayList<>();

    protected JournalEntry() {
    }

    public JournalEntry(LocalDate date, String description, JournalEntryItem items[]) {

        this.items = Arrays.asList(items);
        if (!JournalEntry.isBalanced(this.items)) {
            throw new IllegalArgumentException(
                    "The total of debits must equal the total of credits");
        }

        this.date = date;
        this.description = description;
        addItems(this.items);
    }

    public JournalEntry(LocalDate date, String description, List<JournalEntryItem> items) {
        if (!JournalEntry.isBalanced(items)) {
            throw new IllegalArgumentException(
                    "The total of debits must equal the total of credits");
        }
        this.date = date;
        this.description = description;
        addItems(items);
    }

    public static boolean isBalanced(List<JournalEntryItem> items) {
//        BigDecimal total = BigDecimal.ZERO;
//        for (JournalEntryItem item : items) {
//            if (item.getType() == Type.DEBIT) {
//                total = total.subtract(item.getAmount());
//            } else { // item.type() == Type.CREDIT
//                total = total.add(item.getAmount());
//            }
//        }
//        return total.equals(BigDecimal.ZERO);

        BigDecimal d = items
                .stream()
                .filter(x -> x.isDebit())
                .map(x -> x.getAmount())
                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));

        BigDecimal c = items
                .stream()
                .filter(x -> x.isDebit())
                .map(x -> x.getAmount())
                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
        
        return d.equals(c);
    }

    private void addItems(List<JournalEntryItem> items) {
        this.items = items;
        this.items.forEach(x -> x.setJournalEntry(this));
        calculateJournalTotals();
    }

    // getters and setters removed
    public List<JournalEntryItem> items() {
        return Collections.unmodifiableList(this.items);
    }

    private void calculateJournalTotals() {
        this.amount = items
                .stream()
                .filter(x -> x.isDebit())
                .map(x -> x.getAmount())
                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
        System.err.println(this.amount);
        BigDecimal c = items
                .stream()
                .filter(x -> x.isDebit())
                .map(x -> x.getAmount())
                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
        System.err.println("My credut ... " + c);
    }
}

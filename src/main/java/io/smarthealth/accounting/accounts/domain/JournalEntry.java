package io.smarthealth.accounting.accounts.domain;

import io.smarthealth.accounting.accounts.data.Creditor;
import io.smarthealth.accounting.accounts.data.Debtor;
import io.smarthealth.accounting.accounts.data.JournalEntryData;
import io.smarthealth.accounting.accounts.data.JournalEntryItemData;
import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public JournalEntry(LocalDate date, String description, JournalEntryItem journalEntryItems[]) {
        List<JournalEntryItem> journalsClone = new ArrayList<>(Arrays.asList(journalEntryItems));
        this.items = journalsClone;
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
        BigDecimal d = items
                .stream()
                .filter(x -> x.isDebit())
                .map(x -> x.getDebit())
                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));

        BigDecimal c = items
                .stream()
                .filter(x -> x.isCredit())
                .map(x -> x.getCredit())
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
                .map(x -> x.getDebit())
                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
        System.err.println(this.amount);
        BigDecimal c = items
                .stream()
                .filter(x -> x.isDebit())
                .map(x -> x.getCredit())
                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
//        System.err.println("My credut ... " + c);
    }
    public JournalEntryData toData() {
        final JournalEntryData data = new JournalEntryData();
        data.setId(this.getId());
        data.setTransactionNo(this.transactionNo);
        data.setDate(this.date);
        data.setTransactionType(this.transactionType);
        data.setDescription(this.description);
        data.setAmount(this.amount);
        data.setCreatedBy(this.getCreatedBy());
        Set<Debtor> debtors = new HashSet<>();
        Set<Creditor> creditors = new HashSet<>();
        List<JournalEntryItemData> jeitems=new ArrayList<>();
        this.getItems()
                .stream()
                .forEach(item -> {
                     jeitems.add(item.toData());
                    if (item.isDebit()) {
                        debtors.add(new Debtor(item.getDescription(), item.getAccount().getName(),item.getAccount().getIdentifier(), item.getDebit(), item.getJournalEntry().getTransactionType()));
                    }
                    if (item.isCredit()) {
                        creditors.add(new Creditor(item.getDescription(), item.getAccount().getName(),item.getAccount().getIdentifier(), item.getCredit(),item.getJournalEntry().getTransactionType()));
                    }
                });
        data.setJournalEntries(jeitems);
        data.setDebtors(debtors);
        data.setCreditors(creditors);
        data.setState(this.status);
        return data;
    }
    
      @Override
    public String toString() {
        return "Journal Entry [id=" + getId() + ", description=" + description + ", transaction_no=" + transactionNo + ", date=" + date + ", status=" +status+ " ]";
    }
}

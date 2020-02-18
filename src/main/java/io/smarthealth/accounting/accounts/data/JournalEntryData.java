package io.smarthealth.accounting.accounts.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public final class JournalEntryData {

    private Long id;
    @NotNull
     @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate date=LocalDate.now();
    private String transactionNo;
    private TransactionType transactionType;
    private String description;
    private Set<Debtor> debtors;
    private Set<Creditor> creditors;
    private JournalState state;
    private BigDecimal amount;
    private String createdBy;

    public JournalEntryData() {
        super();
    }

    public static JournalEntryData map(JournalEntry journalEntry) {
        final JournalEntryData data = new JournalEntryData();
        data.setId(journalEntry.getId());
        data.setTransactionNo(journalEntry.getTransactionNo());
        data.setDate(journalEntry.getDate());
        data.setTransactionType(journalEntry.getTransactionType());
        data.setDescription(journalEntry.getDescription());
        data.setAmount(journalEntry.getAmount());
        data.setCreatedBy(journalEntry.getCreatedBy());
        Set<Debtor> debtors = new HashSet<>();
        Set<Creditor> creditors = new HashSet<>();
        journalEntry.getItems()
                .stream()
                .forEach(item -> {
                    if (item.isDebit()) {
                        debtors.add(new Debtor(item.getAccountNumber(), item.getAmount()));
                    }
                    if (item.isCredit()) {
                        creditors.add(new Creditor(item.getAccountNumber(), item.getAmount()));
                    }
                });

        data.setDebtors(debtors);
        data.setCreditors(creditors);
        data.setState(journalEntry.getStatus());
        return data;
    }
}

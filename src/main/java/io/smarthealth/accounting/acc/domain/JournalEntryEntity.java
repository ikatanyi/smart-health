package io.smarthealth.accounting.acc.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity
@Table(name = "acc_journal_entries")
@EqualsAndHashCode(exclude={"debtors", "creditors"}, callSuper = false)
public class JournalEntryEntity extends Auditable {

    @DateTimeFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate dateBucket;
    private String journalNumber;
    @DateTimeFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transactionDate;
    private String transactionType;
    private String transactionNo;
    private String clerk;
    private String note;
    @OneToMany(mappedBy = "journalEntryEntity", cascade = CascadeType.ALL)
    private Set<DebtorType> debtors = new HashSet<>();
    @OneToMany(mappedBy = "journalEntryEntity", cascade = CascadeType.ALL)
    private Set<CreditorType> creditors = new HashSet<>();
    private String state;
    private String message;

    public JournalEntryEntity() {
        super();
    }
    public void addDebtors(Set<DebtorType> debtorTypes){
        this.debtors=debtorTypes;
        this.debtors.forEach(x->x.setJournalEntryEntity(this));
    }

    public void addCreditors(Set<CreditorType> creditorTypes){
        this.creditors=creditorTypes;
        this.creditors.forEach(x -> x.setJournalEntryEntity(this));
    }
    public Double getJournalAmount() {
        return this.debtors
                .stream()
                .map(x -> x.getAmount())
                .collect(Collectors.summingDouble(Double::doubleValue));
//                .reduce(0D, (a, b) -> a + b);
    }
}

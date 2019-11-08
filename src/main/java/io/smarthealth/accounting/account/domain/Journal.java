package io.smarthealth.accounting.account.domain;

import io.smarthealth.accounting.account.domain.enumeration.JournalState;
import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_journal")
public class Journal extends Auditable {

    private LocalDate transactionDate;
    private String transactionId;
    private String transactionType;
    private LocalDate documentDate;
    private String activity; // financial activity accured - Department
    private String descriptions; //A description associated with this entry
    private String referenceNumber; //An additional field that is used to store additional information about the entry (Ex: chequeNo, receipt no) 
    private boolean manualEntry; //Flag determines if an entry is generated by system or entered manually
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reversal_id",foreignKey = @ForeignKey(name = "fk_journal_reversal_id"))
    private Journal reversalJournal;
    private boolean reversed;
    
    @Enumerated(EnumType.STRING)
    private JournalState state;
    
    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL)
    private List<JournalEntry> journalEntries = new ArrayList<>();
 
    public void addJournalEntry(JournalEntry journalEntry) {
        journalEntries.add(journalEntry);
        journalEntry.setJournal(this);
    }
    
    //this is the values that can be defined and n
    //I have a transactions
}

/*
- transaction id
- transaction date
- transaction description
- transaction type
- referenceNumber
- journal status
- 
*/

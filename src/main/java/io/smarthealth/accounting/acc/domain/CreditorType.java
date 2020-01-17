package io.smarthealth.accounting.acc.domain;


import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "acc_creditor") 
public class CreditorType extends Identifiable{
 
  private String accountNumber; 
  private Double amount;
    @ManyToOne
    @JoinColumn(name = "journal_entry_id", foreignKey = @ForeignKey(name = "fk_creditors_journal_entry_id"))
    private JournalEntryEntity journalEntryEntity;

  public CreditorType() {
    super();
  }

    public CreditorType(String accountNumber, Double amount) {
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public CreditorType(String accountNumber, Double amount, JournalEntryEntity journalEntryEntity) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.journalEntryEntity = journalEntryEntity;
    }
  
}

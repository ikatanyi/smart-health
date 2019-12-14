package io.smarthealth.accounting.acc.domain;


import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.*;
import lombok.Data;


@Data
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
 
}

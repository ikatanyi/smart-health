package io.smarthealth.accounting.acc.data.mapper;


import io.smarthealth.accounting.acc.data.v1.Creditor;
import io.smarthealth.accounting.acc.data.v1.Debtor;
import io.smarthealth.accounting.acc.data.v1.JournalEntry;
import io.smarthealth.accounting.acc.domain.JournalEntryEntity;
import java.util.stream.Collectors;

public class JournalEntryMapper {

  private JournalEntryMapper() {
    super();
  }

  public static JournalEntry map(final JournalEntryEntity journalEntryEntity) {
    final JournalEntry journalEntry = new JournalEntry();
    journalEntry.setJournalNumber(journalEntryEntity.getJournalNumber());
    journalEntry.setTransactionDate(journalEntryEntity.getTransactionDate());
    journalEntry.setTransactionType(journalEntryEntity.getTransactionType());
    journalEntry.setClerk(journalEntryEntity.getClerk());
    journalEntry.setNote(journalEntryEntity.getNote());
    journalEntry.setDebtors(
        journalEntryEntity.getDebtors()
            .stream()
            .map(debtorType -> {
              final Debtor debtor = new Debtor();
              debtor.setAccountNumber(debtorType.getAccountNumber());
              debtor.setAmount(Double.toString(debtorType.getAmount()));
              return debtor;
            })
            .collect(Collectors.toSet())
    );
    journalEntry.setCreditors(
        journalEntryEntity.getCreditors()
            .stream()
            .map(creditorType -> {
              final Creditor creditor = new Creditor();
              creditor.setAccountNumber(creditorType.getAccountNumber());
              creditor.setAmount(Double.toString(creditorType.getAmount()));
              return creditor;
            })
            .collect(Collectors.toSet())
    );
    journalEntry.setMessage(journalEntryEntity.getMessage());
    journalEntry.setState(journalEntryEntity.getState());
    return journalEntry;
  }
}

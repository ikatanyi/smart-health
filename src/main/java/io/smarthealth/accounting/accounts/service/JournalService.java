package io.smarthealth.accounting.accounts.service;

import io.smarthealth.accounting.accounts.data.JournalEntryData;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalRepository;
import io.smarthealth.accounting.accounts.domain.JournalReversal;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.domain.specification.JournalSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalRepository journalRepository;
    private final AccountService accountService;
    private final SequenceNumberService sequenceNumberService;

    @Transactional
    public JournalEntry createJournal(JournalEntryData data) {

        List<JournalEntryItem> items = data.getDebtors()
                .stream()
                .map(x -> {
                    Account account = accountService.findByAccountNumberOrThrow(x.getAccountNumber());
                    return new JournalEntryItem(account, x.getDescription(), x.getAmount(), BigDecimal.ZERO);
                })
                .collect(Collectors.toList());

        items.addAll(data.getCreditors()
                .stream()
                .map(x -> {
                    Account account = accountService.findByAccountNumberOrThrow(x.getAccountNumber());
                    return new JournalEntryItem(account, x.getDescription(), BigDecimal.ZERO, x.getAmount());
                })
                .collect(Collectors.toList())
        );

        JournalEntry toSave = new JournalEntry(data.getDate(), data.getDescription(), items);
        toSave.setTransactionType(data.getTransactionType());
        toSave.setStatus(JournalState.PENDING);
        if (data.getTransactionNo() == null) {
            toSave.setTransactionNo(sequenceNumberService.next(1L, Sequences.Transactions.name()));
        }
        JournalEntry je = save(toSave);
//        bookJournalEntry(je.getId());
        return je;
    }

    public JournalEntry save(JournalEntry journal) {
        JournalEntry je = journalRepository.save(journal);
        bookJournalEntry(je.getId());
        return je;
    }

    public Page<JournalEntryData> findJournals(String transactionNo, TransactionType type, JournalState status, DateRange range, String accountNo, Pageable page) {
        Account account = null;
        if(accountNo!=null){
            account = accountService.findByAccountNumberOrThrow(accountNo);
        }
        Specification<JournalEntry> spec = JournalSpecification.createSpecification(transactionNo, type, status, range, account);
        return journalRepository.findAll(spec, page)
                .map(x -> x.toData());
    }

    public Optional<JournalEntry> findJournalById(Long id) {
        return journalRepository.findById(id);
    }

    public Optional<JournalEntry> findJournalByTransactionNo(String transactionNo) {
        return journalRepository.findByTransactionNo(transactionNo);
    }

    public JournalEntry findJournalIdOrThrow(Long id) {
        return findJournalById(id)
                .orElseThrow(() -> APIException.notFound("Journal entry {0} not found.", id));
    }

    private String bookJournalEntry(Long journalId) {
        final Optional<JournalEntry> optionalJournalEntry = findJournalById(journalId);

        if (!optionalJournalEntry.isPresent()) {
            return null;
        }
        final JournalEntry journal = optionalJournalEntry.get();
        if (journal.getStatus() != JournalState.PENDING) {
            return null;
        }

        journal.getItems()
                .stream()
                .forEach(je -> {
                    final Account accountEntity = je.getAccount();// accountService.findByAccountNumberOrThrow(je.getAccountNumber());
//                    System.out.println("accountEntity "+accountEntity.getName());
                    final BigDecimal amount;
                    switch (accountEntity.getType()) {
                        case ASSET:
                        case EXPENSE:
                            if (je.isDebit()) {
                                accountEntity.setBalance(accountEntity.getBalance().add(je.getDebit()));
                                amount = je.getDebit();
                            } else {
                                accountEntity.setBalance(accountEntity.getBalance().subtract(je.getCredit()));
                                amount = je.getCredit().negate();
                            }
                            break;
                        case LIABILITY:
                        case EQUITY:
                        case REVENUE:
                            if (je.isDebit()) {
                                accountEntity.setBalance(accountEntity.getBalance().subtract(je.getDebit()));
                                amount = je.getDebit().negate();
                            } else {
                                accountEntity.setBalance(accountEntity.getBalance().add(je.getCredit()));
                                amount = je.getCredit();
                            }
                            break;
                        default:
                            amount = BigDecimal.ZERO;
                    }
                    accountService.adjustLedgerTotals(accountEntity.getLedger().getIdentifier(), amount);

                });

        releaseJournalEntry(journalId);
        return journal.getTransactionNo();
    }

    public void releaseJournalEntry(Long journalId) {
        final Optional<JournalEntry> optionalJournalEntry = findJournalById(journalId);
        if (optionalJournalEntry.isPresent()) {
            final JournalEntry journalEntryEntity = optionalJournalEntry.get();
            journalEntryEntity.setStatus(JournalState.PROCESSED);
            journalRepository.save(journalEntryEntity);
        }
    }

    public JournalEntry reverseJournal(Long journalId, JournalReversal journalReversal) {
        final Optional<JournalEntry> optionalJournalEntry = findJournalById(journalId);
        if (optionalJournalEntry.isPresent()) {
            JournalEntry journalEntryEntity = optionalJournalEntry.get();

            List<JournalEntryItem> items = journalEntryEntity.getItems()
                    .stream()
                    .map(je -> createJournalItem(je))
                    .collect(Collectors.toList());

            String description = journalEntryEntity.getDescription() != null ? journalEntryEntity.getDescription()+"(Reversed Transaction)" : "Journal Reversal - Journal Entry: " + journalEntryEntity.getId();
            JournalEntry toSave = new JournalEntry(journalReversal.getDate(), description, items);
            toSave.setTransactionType(TransactionType.Journal_Entry_Reversal);
            toSave.setStatus(JournalState.PENDING);

            if (journalReversal.getTransactionNo() == null) {
                toSave.setTransactionNo(sequenceNumberService.next(1L, Sequences.Transactions.name()));
            } else {
                toSave.setTransactionNo(journalReversal.getTransactionNo());
            }

            JournalEntry savedJOurnal = save(toSave);

            journalEntryEntity.setReversed(true);
            journalEntryEntity.setReversalJournalEntry(savedJOurnal);
            journalRepository.save(journalEntryEntity);

            return savedJOurnal;
        }

        return null;
    }

    private JournalEntryItem createJournalItem(JournalEntryItem je) {
        return new JournalEntryItem(je.getAccount(), je.getDescription() + "(Reversed Transaction)", je.getCredit(), je.getDebit());
    } 
     //can I have my journals here for
}

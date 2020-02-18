package io.smarthealth.accounting.accounts.service;

import io.smarthealth.accounting.accounts.data.JournalEntryData;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalRepository;
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
                .map(x -> new JournalEntryItem(x.getAccountNumber(), JournalEntryItem.Type.DEBIT, x.getAmount()))
                .collect(Collectors.toList());

        items.addAll(data.getCreditors()
                .stream()
                .map(x -> new JournalEntryItem(x.getAccountNumber(), JournalEntryItem.Type.CREDIT, x.getAmount()))
                .collect(Collectors.toList())
        );

        JournalEntry toSave = new JournalEntry(data.getDate(), data.getDescription(), items);
        toSave.setTransactionType(data.getTransactionType());
        toSave.setStatus(JournalState.PENDING);
        if(data.getTransactionNo()==null){
            toSave.setTransactionNo(sequenceNumberService.next(1L, Sequences.Transactions.name()));
        }
        JournalEntry je = save(toSave);
//        bookJournalEntry(je.getId());
        return je;
    }

    public JournalEntry save(JournalEntry journal) {
//        if (journal.getTransactionNo() == null) {
//            journal.setTransactionNo(sequenceNumberService.next(1L, Sequences.Transactions.name()));
//        }
         JournalEntry je = journalRepository.save(journal);
        bookJournalEntry(je.getId());
        return je;
    }

    public Page<JournalEntryData> findJournals(String transactionNo, TransactionType type, JournalState status, DateRange range, Pageable page) {
        Specification<JournalEntry> spec = JournalSpecification.createSpecification(transactionNo, type, status, range);
        return journalRepository.findAll(spec, page)
                .map(x -> JournalEntryData.map(x));
    }

    public Optional<JournalEntry> findJournalById(Long id) {
        return journalRepository.findById(id);
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
                    final Account accountEntity = accountService.findByAccountNumberOrThrow(je.getAccountNumber());
                    final BigDecimal amount;
                    switch (accountEntity.getType()) {
                        case ASSET:
                        case EXPENSE:
                            if (je.isDebit()) {
                                accountEntity.setBalance(accountEntity.getBalance().add(je.getAmount()));
                                amount = je.getAmount();
                            } else {
                                accountEntity.setBalance(accountEntity.getBalance().subtract(je.getAmount()));
                                amount = je.getAmount().negate();
                            }
                            break;
                        case LIABILITY:
                        case EQUITY:
                        case REVENUE:
                            if (je.isDebit()) {
                                accountEntity.setBalance(accountEntity.getBalance().subtract(je.getAmount()));
                                amount = je.getAmount().negate();
                            } else {
                                accountEntity.setBalance(accountEntity.getBalance().add(je.getAmount()));
                                amount = je.getAmount();
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

}

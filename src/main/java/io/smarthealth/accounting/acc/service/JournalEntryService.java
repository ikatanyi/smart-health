package io.smarthealth.accounting.acc.service;

import io.smarthealth.accounting.acc.data.mapper.JournalEntryMapper;
import io.smarthealth.accounting.acc.data.v1.*;
import io.smarthealth.accounting.acc.domain.*;
import io.smarthealth.accounting.acc.events.JournalEvent;
import io.smarthealth.infrastructure.lang.DateRange;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JournalEntryService {

    private final JournalEntrysRepository journalEntryRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final AccountService accountServices;
    private final JournalEventSender journalEventSender;

    public JournalEntryService(JournalEntrysRepository journalEntryRepository, TransactionTypeRepository transactionTypeRepository, AccountService accountServices, JournalEventSender journalEventSender) {
        this.journalEntryRepository = journalEntryRepository;
        this.transactionTypeRepository = transactionTypeRepository;
        this.accountServices = accountServices;
        this.journalEventSender = journalEventSender;
    }

    private List<JournalEntryEntity> fetchJournalEntriesByDate(DateRange range) {
        return journalEntryRepository.findByDateBucketBetween(range.getStartDateTime().toLocalDate(), range.getEndDateTime().toLocalDate());
    }

    public List<JournalEntry> fetchJournalEntries(final DateRange range, final String accountNumber, final BigDecimal amount) {
        List<JournalEntryEntity> journalEntryEntities = fetchJournalEntriesByDate(range);

        if (journalEntryEntities != null) {

            final List<JournalEntryEntity> filteredList
                    = journalEntryEntities
                            .stream()
                            .filter(journalEntryEntity
                                    -> accountNumber == null
                            || journalEntryEntity.getDebtors().stream()
                                    .anyMatch(debtorType -> debtorType.getAccountNumber().equals(accountNumber))
                            || journalEntryEntity.getCreditors().stream()
                                    .anyMatch(creditorType -> creditorType.getAccountNumber().equals(accountNumber))
                            )
                            .filter(journalEntryEntity
                                    -> amount == null
                            || amount.compareTo(
                                    BigDecimal.valueOf(
                                            journalEntryEntity.getDebtors().stream().mapToDouble(DebtorType::getAmount).sum()
                                    )
                            ) == 0
                            )
                            .sorted(Comparator.comparing(JournalEntryEntity::getTransactionDate))
                            .collect(Collectors.toList());

            final List<TransactionTypeEntity> transactionTypes = this.transactionTypeRepository.findAll();
            final HashMap<String, String> mappedTransactionTypes = new HashMap<>(transactionTypes.size());
            transactionTypes.forEach(transactionTypeEntity
                    -> mappedTransactionTypes.put(transactionTypeEntity.getIdentifier(), transactionTypeEntity.getName())
            );

            return filteredList
                    .stream()
                    .map(journalEntryEntity -> {
                        final JournalEntry journalEntry = JournalEntryMapper.map(journalEntryEntity);
                        journalEntry.setTransactionType(mappedTransactionTypes.get(journalEntry.getTransactionType()));
                        return journalEntry;
                    })
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public Optional<JournalEntry> findJournalEntry(final String journalNo) {
        final Optional<JournalEntryEntity> optionalJournalEntryEntity = findJournalEntryEntity(journalNo);

        return optionalJournalEntryEntity.map(JournalEntryMapper::map);
    }

    public Optional<JournalEntryEntity> findJournalEntryEntity(final String journalNo) {
        return this.journalEntryRepository
                .findByJournalNumber(journalNo);
    }

    @Transactional
    public JournalEntry createJournalEntry(JournalEntry createJournalEntryCommand) {
        final JournalEntry journalEntry = createJournalEntryCommand;
        journalEntry.setState("PENDING");

        final Set<Debtor> debtors = journalEntry.getDebtors();
        final Set<DebtorType> debtorTypes = debtors
                .stream()
                .map(debtor -> {
                    final DebtorType debtorType = new DebtorType();
                    debtorType.setAccountNumber(debtor.getAccountNumber());
                    debtorType.setAmount(Double.valueOf(debtor.getAmount()));
                    return debtorType;
                })
                .collect(Collectors.toSet());
        final Set<Creditor> creditors = journalEntry.getCreditors();
        final Set<CreditorType> creditorTypes = creditors
                .stream()
                .map(creditor -> {
                    final CreditorType creditorType = new CreditorType();
                    creditorType.setAccountNumber(creditor.getAccountNumber());
                    creditorType.setAmount(Double.valueOf(creditor.getAmount()));
                    return creditorType;
                })
                .collect(Collectors.toSet());
        final JournalEntryEntity journalEntryEntity = new JournalEntryEntity();
        String journalid = generateJournalNumber(); //RandomStringUtils.randomAlphanumeric(32)
//        journalEntryEntity.setTransactionIdentifier(journalEntry.getTransactionIdentifier());
        journalEntryEntity.setJournalNumber(journalid);
        final LocalDateTime transactionDate = journalEntry.getTransactionDate();
        journalEntryEntity.setDateBucket(transactionDate.toLocalDate());
        journalEntryEntity.setTransactionNo(journalEntry.getTransactionNo());
        journalEntryEntity.setTransactionDate(transactionDate);
        journalEntryEntity.setTransactionType(journalEntry.getTransactionType());
        journalEntryEntity.addDebtors(debtorTypes);
        journalEntryEntity.addCreditors(creditorTypes);
        journalEntryEntity.setMessage(journalEntry.getMessage());
        journalEntryEntity.setState(JournalEntry.State.PENDING.name());

        JournalEntryEntity jee = journalEntryRepository.save(journalEntryEntity);

//    this.commandGateway.process(new BookJournalEntryCommand(journalEntry.getTransactionIdentifier()));
//        bookJournalEntry(jee.getTransactionIdentifier());
//        journalSender.postJournal(journalEntry); 
        journalEventSender.process(new JournalEvent(jee.getJournalNumber()));

        return JournalEntryMapper.map(jee);
//        return journalEntry.getTransactionIdentifier();
    }

    @Transactional
    public String bookJournalEntry(String transactionIdentifier) {

        final Optional<JournalEntryEntity> optionalJournalEntry = this.findJournalEntryEntity(transactionIdentifier);

        if (optionalJournalEntry.isPresent()) {
            final JournalEntryEntity journalEntryEntity = optionalJournalEntry.get();
            if (!journalEntryEntity.getState().equals(JournalEntry.State.PENDING.name())) {
                return null;
            }
            // process all debtors
            journalEntryEntity.getDebtors()
                    .forEach(debtor -> {
                        final String accountNumber = debtor.getAccountNumber();
                        final AccountEntity accountEntity = this.accountServices.getAccountEntity(accountNumber);
                        final AccountType accountType = AccountType.valueOf(accountEntity.getType());
                        final BigDecimal amount;
                        switch (accountType) {
                            case ASSET:
                            case EXPENSE:
                                accountEntity.setBalance(accountEntity.getBalance() + debtor.getAmount());
                                amount = BigDecimal.valueOf(debtor.getAmount());
                                break;
                            case LIABILITY:
                            case EQUITY:
                            case REVENUE:
                                accountEntity.setBalance(accountEntity.getBalance() - debtor.getAmount());
                                amount = BigDecimal.valueOf(debtor.getAmount()).negate();
                                break;
                            default:
                                amount = BigDecimal.ZERO;
                        }
                        final AccountEntity savedAccountEntity = this.accountServices.save(accountEntity);
                        final AccountEntryEntity accountEntryEntity = new AccountEntryEntity();
                        accountEntryEntity.setType(AccountEntry.Type.DEBIT.name());
                        accountEntryEntity.setAccount(savedAccountEntity);
                        accountEntryEntity.setBalance(savedAccountEntity.getBalance());
                        accountEntryEntity.setAmount(debtor.getAmount());
                        accountEntryEntity.setMessage(journalEntryEntity.getMessage());
                        accountEntryEntity.setTransactionDate(journalEntryEntity.getTransactionDate());

                        this.accountServices.saveAccountEntry(accountEntryEntity);

                        this.accountServices.adjustLedgerTotals(savedAccountEntity.getLedger().getIdentifier(), amount);
                    });
            // process all creditors
            journalEntryEntity.getCreditors()
                    .forEach(creditor -> {
                        final String accountNumber = creditor.getAccountNumber();
                        final AccountEntity accountEntity = this.accountServices.getAccountEntity(accountNumber);
                        final AccountType accountType = AccountType.valueOf(accountEntity.getType());
                        final BigDecimal amount;
                        switch (accountType) {
                            case ASSET:
                            case EXPENSE:
                                accountEntity.setBalance(accountEntity.getBalance() - creditor.getAmount());
                                amount = BigDecimal.valueOf(creditor.getAmount()).negate();
                                break;
                            case LIABILITY:
                            case EQUITY:
                            case REVENUE:
                                accountEntity.setBalance(accountEntity.getBalance() + creditor.getAmount());
                                amount = BigDecimal.valueOf(creditor.getAmount());
                                break;
                            default:
                                amount = BigDecimal.ZERO;
                        }

                        final AccountEntity savedAccountEntity = this.accountServices.save(accountEntity);

                        final AccountEntryEntity accountEntryEntity = new AccountEntryEntity();
                        accountEntryEntity.setType(AccountEntry.Type.CREDIT.name());
                        accountEntryEntity.setAccount(savedAccountEntity);
                        accountEntryEntity.setBalance(savedAccountEntity.getBalance());
                        accountEntryEntity.setAmount(creditor.getAmount());
                        accountEntryEntity.setMessage(journalEntryEntity.getMessage());
                        accountEntryEntity.setTransactionDate(journalEntryEntity.getTransactionDate());

                        this.accountServices.saveAccountEntry(accountEntryEntity);
                        this.accountServices.adjustLedgerTotals(savedAccountEntity.getLedger().getIdentifier(), amount);
                    });

//      this.commandGateway.process(new ReleaseJournalEntryCommand(transactionIdentifier));
            this.releaseJournalEntry(transactionIdentifier);
            return transactionIdentifier;
        } else {
            return null;
        }
    }

    @Transactional
    private String generateJournalNumber() {
        String trxId = RandomStringUtils.randomNumeric(5);//sequenceService.nextNumber(SequenceType.JournalNumber);
        trxId = String.format("ACC-JV-%s", trxId); //acc-jv-2019-0001
        return trxId;
    }

    @Transactional
    public void releaseJournalEntry(String transactionIdentifier) {
        final Optional<JournalEntryEntity> optionalJournalEntry = findJournalEntryEntity(transactionIdentifier);
        if (optionalJournalEntry.isPresent()) {
            final JournalEntryEntity journalEntryEntity = optionalJournalEntry.get();
            journalEntryEntity.setState(JournalEntry.State.PROCESSED.name());
            this.journalEntryRepository.save(journalEntryEntity);
        }
    }

}

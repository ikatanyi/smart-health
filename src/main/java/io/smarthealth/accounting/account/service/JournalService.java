package io.smarthealth.accounting.account.service;

import io.smarthealth.accounting.account.data.JournalData;
import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.accounting.account.domain.Journal;
import io.smarthealth.accounting.account.domain.JournalEntry;
import io.smarthealth.accounting.account.domain.JournalRepository;
import io.smarthealth.accounting.account.domain.enumeration.JournalState;
import io.smarthealth.accounting.account.domain.enumeration.TransactionType;
import io.smarthealth.accounting.account.domain.specification.JournalSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import java.time.LocalDate;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
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
public class JournalService {

    private final JournalRepository journalRepository;
    private final JournalBalanceUpdateService balanceUpdateService;
    private final AccountService accountService;
    private final SequenceService sequenceService;

    public JournalService(JournalRepository journalRepository, JournalBalanceUpdateService balanceUpdateService, AccountService accountService, SequenceService sequenceService) {
        this.journalRepository = journalRepository;
        this.balanceUpdateService = balanceUpdateService;
        this.accountService = accountService;
        this.sequenceService = sequenceService;
    }

    @Transactional
    public Journal createJournalEntry(JournalData journalData) {

        if (journalData.getTransactionId() != null && findJournalEntry(journalData.getTransactionId()).isPresent()) {
            throw APIException.conflict("Journal entry {0} already exists.", journalData.getTransactionId());
        }
        
        validateBusinessRulesForJournalEntries(journalData);

        Journal journal = convertToEntity(journalData);
        journal.setState(JournalState.DRAFT);
        journal.setTransactionId(journalNumber());
//           journal.setTransactionId(sequenceService.nextNumber(SequenceType.JournalNumber));       
        Journal savedJournal = journalRepository.save(journal);
        
        balanceUpdateService.updateRunningBalance();
        
        return savedJournal;
    }
    

    public Optional<Journal> findJournalEntry(final String transactionIdentifier) {
        return journalRepository.findByTransactionId(transactionIdentifier);
    }

    public JournalData findJournalDataEntry(final String transactionIdentifier) {
        Journal journal = findJournalEntry(transactionIdentifier)
                .orElseThrow(() -> APIException.notFound("Journal number {0} not found.", transactionIdentifier));

        return JournalData.map(journal);
    }

    public Page<JournalData> fetchJournalEntries(String referenceNumber, String transactionId, String transactionType, DateRange range, Pageable pageable) {
        Specification<Journal> spec = JournalSpecification.createSpecification(referenceNumber, transactionId, transactionType, range);
        Page<Journal> journals = journalRepository.findAll(spec, pageable);
        return journals.map(journal -> JournalData.map(journal));
    }

    private Journal convertToEntity(JournalData journalData) {
        Journal journal = new Journal();

        journal.setActivity(journalData.getActivity());
        journal.setDescriptions(journalData.getDescriptions());
        journal.setDocumentDate(journalData.getDocumentDate());
        journal.setManualEntry(journalData.isManualEntry());
        journal.setReferenceNumber(journalData.getReferenceNumber());
        journal.setState(journalData.getState());
        journal.setTransactionDate(journalData.getTransactionDate());
        journal.setTransactionId(journalData.getTransactionId());

        journalData
                .getJournalEntries()
                .stream()
                .map(jd -> new JournalEntry(
                        getAccount(jd.getAccountNumber()),
                        jd.getCredit(),
                        jd.getDebit(),
                        journalData.getTransactionDate(),
                        journalData.getTransactionId()
                ))
                .forEach(je -> journal.addJournalEntry(je));

        return journal;
    }

    @Transactional
    public String revertJournalEntry(String transactionId, String reversalComment) {

        final String reversalTransactionId =journalNumber(); //generateTransactionId(2L);// sequenceService.nextNumber(SequenceType.JournalNumber);//generateTransactionId(2L);
        final boolean manualEntry = true;
        final boolean useDefaultComment = StringUtils.isBlank(reversalComment);

        Journal journalEntry = journalRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> APIException.notFound("Transactions {0} Not Found", transactionId));
       
        if (useDefaultComment) {
            reversalComment = "Reversal entry for Journal Entry with Entry Id  :" + journalEntry.getId() + " and transaction Id " + journalEntry.getTransactionId();
        }

        Journal reversalJournal = journalEntry.getJournalReversal(reversalTransactionId, reversalComment, manualEntry);
        reversalJournal.setTransactionType(TransactionType.Journal_Reversal); 
 
        Journal savedJournal = journalRepository.save(reversalJournal);

        journalEntry.setReversed(true);
        journalEntry.setState(JournalState.REVERSED);
        journalEntry.setReversalJournal(savedJournal);

        journalRepository.save(journalEntry);
        
          balanceUpdateService.updateRunningBalance();
        
        return reversalTransactionId;
    }
    
    private String journalNumber(){
        String trxId=sequenceService.nextNumber(SequenceType.JournalNumber);
        trxId=String.format("ACC-JV-%s-%s", String.valueOf(LocalDate.now().getYear()), trxId);
        return trxId;
    }

    public static String generateTransactionId(final Long companyId) {
        //journal format : ACC-JV-2019-00001
//        Long id = SecurityUtils.getCurrentLoggedUserId().get();
         
        final Long time = System.currentTimeMillis();
        final String uniqueVal = String.valueOf(time) + 120L + companyId;
        final String transactionId = Long.toHexString(Long.parseLong(uniqueVal));
        return transactionId;
    }

    private Account getAccount(String accountNo) {
        return accountService.findOneWithNotFoundDetection(accountNo);
    }

    public void doJournalBalances() {
        balanceUpdateService.updateRunningBalance();
    }

    private void validateAccountForTransaction(final Account account) {
        /**
         * *
         * validate that the account allows manual adjustments and is not
         * disabled
         *
         */
        if (!account.getEnabled()) {
            throw APIException.badRequest("Target account is not enabled");
        }
    }

    private void validateBusinessRulesForJournalEntries(JournalData journal) {
        if (journal.getTransactionDate().isAfter(LocalDate.now())) {
            throw APIException.badRequest("The journal entry cannot be made for a future date");
        }
        // atleast one debit or credit must be present 
        Double debts = journal.getJournalEntries()
                .stream()
                .peek(debtor -> {
                    final Optional<Account> accountOptional = accountService.findAccount(debtor.getAccountNumber());
                    if (!accountOptional.isPresent()) {
                        throw APIException.badRequest("Unknown debtor account {0}.", debtor.getAccountNumber());
                    }
                    if (!accountOptional.get().getEnabled()) {
                        throw APIException.badRequest("Debtor account {0} must be enabled for Transaction", debtor.getAccountNumber());
                    }  
                })
                .map(je -> je.getDebit())
                .reduce(0D, Double::sum);

        Double credits = journal.getJournalEntries()
                .stream()
                .peek(creditor -> {
                    final Optional<Account> accountOptional = accountService.findAccount(creditor.getAccountNumber());
                    if (!accountOptional.isPresent()) {
                        throw APIException.badRequest("Unknown creditor account{0}.", creditor.getAccountNumber());
                    }
                    if (!accountOptional.get().getEnabled()) {
                        throw APIException.badRequest("Creditor account {0} must be enabled for Transaction.", creditor.getAccountNumber());
                    }
                })
                .map(je -> je.getCredit())
                .reduce(0D, Double::sum);

        if (debts == 0 || credits == 0) {
            throw APIException.badRequest("Journal Entry must have atleast one Debit and one Credit");
        }

        if (debts == 0 && credits == 0) {
            throw APIException.badRequest("Both account and amount must be specified for all Debits and Credits");
        }

        if (!debts.equals(credits)) {
            throw APIException.badRequest("Sum of debtor and sum of creditor amounts must be equals.");
        }

    }
}

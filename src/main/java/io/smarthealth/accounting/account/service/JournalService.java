package io.smarthealth.accounting.account.service;

import io.smarthealth.accounting.account.data.Credit;
import io.smarthealth.accounting.account.data.Debit;
import io.smarthealth.accounting.account.data.JournalData;
import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.accounting.account.domain.AccountRepository;
import io.smarthealth.accounting.account.domain.Journal;
import io.smarthealth.accounting.account.domain.JournalEntry;
import io.smarthealth.accounting.account.domain.JournalRepository;
import io.smarthealth.accounting.account.domain.specification.JournalSpecification;
import io.smarthealth.accounting.payment.domain.PaymentDetail;
import io.smarthealth.accounting.payment.domain.PaymentDetailRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class JournalService {

    private final JournalRepository journalRepository;
    private final PaymentDetailRepository paymentDetailRepository;
    private final AccountRepository accountRepository;

    public JournalService(JournalRepository journalRepository, PaymentDetailRepository paymentDetailRepository, AccountRepository accountRepository) {
        this.journalRepository = journalRepository;
        this.paymentDetailRepository = paymentDetailRepository;
        this.accountRepository = accountRepository;
    }

    public String createJournalEntry(JournalData journalData) {
        Journal journal = convertToEntity(journalData);
        journal.setTransactionId(generateTransactionId(2L));
        Journal savedJournal = journalRepository.save(journal);
        return savedJournal.getTransactionId();
    }

    public Optional<Journal> findJournalEntry(final String transactionIdentifier) {
        return journalRepository.findByTransactionId(transactionIdentifier);
    }

    public JournalData findJournalDataEntry(final String transactionIdentifier) {
        Journal journal = findJournalEntry(transactionIdentifier)
                .orElseThrow(() -> APIException.notFound("Journal {0} not found.", transactionIdentifier));

        return convertToData(journal);
    }

    public Page<JournalData> fetchJournalEntries(String referenceNumber, String transactionId, String transactionType, DateRange range, Pageable pageable) {
        Specification<Journal> spec = JournalSpecification.createSpecification(referenceNumber, transactionId, transactionType, range);
        Page<Journal> journals = journalRepository.findAll(spec, pageable);
        return journals.map(journal -> convertToData(journal));
    }

    private Journal convertToEntity(JournalData journalData) {
        Journal journal = new Journal();
        Optional<PaymentDetail> paymentdetail = paymentDetailRepository.findById(journalData.getPaymentDetail());
        journal.setActivity(journalData.getActivity());
        journal.setDescriptions(journalData.getDescriptions());
        journal.setDocumentDate(journalData.getDocumentDate());
        journal.setManualEntry(journalData.isManualEntry());
        if (paymentdetail.isPresent()) {
            journal.setPaymentDetail(paymentdetail.get());
        }
        journal.setReferenceNumber(journalData.getReferenceNumber());
        journal.setState(journalData.getState());
        journal.setTransactionDate(journalData.getTransactionDate());
        journal.setTransactionId(journalData.getTransactionId());
        journal.setTransactionType(journalData.getTransactionType());

        journalData.getDebit()
                .stream()
                .map(jd -> new JournalEntry(getAccount(jd.getAccountNumber()), 0.00, Double.valueOf(jd.getAmount()), journalData.getTransactionDate(), jd.getDescription()))
                .forEach(je -> journal.addJournalEntry(je));

        journalData.getCredit()
                .stream()
                .map(jd -> new JournalEntry(getAccount(jd.getAccountNumber()), Double.valueOf(jd.getAmount()), 0.00, journalData.getTransactionDate(),jd.getDescription()))
                .forEach(je -> journal.addJournalEntry(je));

        return journal;
    }

    public String revertJournalEntry(String transactionId, String reversalComment) {
        
        final String reversalTransactionId = generateTransactionId(2L);
        final boolean manualEntry = true;
        final boolean useDefaultComment = StringUtils.isBlank(reversalComment);

        Journal journalEntry = journalRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> APIException.notFound("Transactions {0} Not Found", transactionId));
        // check if the period is closed
        
//        if(journalEntry.isReversed()){
//            throw APIException.badRequest("Transaction Id {0} is already Reversed", transactionId);
//        }
        if (useDefaultComment) {
            reversalComment = "Reversal entry for Journal Entry with Entry Id  :" + journalEntry.getId() + " and transaction Id " + journalEntry.getTransactionId();
        }
        Journal reversalJournal = new Journal();
        reversalJournal.setDescriptions(reversalComment);
        reversalJournal.setActivity(journalEntry.getActivity());
        reversalJournal.setDocumentDate(journalEntry.getDocumentDate());
        reversalJournal.setManualEntry(manualEntry);
        reversalJournal.setPaymentDetail(journalEntry.getPaymentDetail());
        reversalJournal.setReferenceNumber(journalEntry.getReferenceNumber());
        reversalJournal.setTransactionDate(journalEntry.getTransactionDate());
        reversalJournal.setTransactionId(reversalTransactionId);

        journalEntry.getJournalEntries()
                .forEach((je) -> {
                    if (je.isDebit()) {
                        reversalJournal.addJournalEntry(new JournalEntry(je.getAccount(), je.getDebit(), 0.0D, je.getEntryDate(),je.getDescription()));
                    } else {
                        reversalJournal.addJournalEntry(new JournalEntry(je.getAccount(), 0.0D, je.getCredit(), je.getEntryDate(),je.getDescription()));
                    }
                });
         Journal savedJournal = journalRepository.save(reversalJournal);
         
        journalEntry.setReversed(true);
        journalEntry.setState(JournalData.State.REVERSED);
        journalEntry.setReversalJournal(savedJournal);
        
        journalRepository.save(journalEntry);
        return reversalTransactionId;
    }

    private JournalData convertToData(Journal journal) {
        JournalData journalData = new JournalData();
        journalData.setActivity(journal.getActivity());
        journalData.setDescriptions(journal.getDescriptions());
        journalData.setDocumentDate(journal.getDocumentDate());
        journalData.setManualEntry(journal.isManualEntry());
        if (journal.getPaymentDetail() != null) {
            journalData.setPaymentDetail(journal.getPaymentDetail().getId());
        }
        journalData.setReferenceNumber(journal.getReferenceNumber());
        journalData.setState(journal.getState());
        journalData.setTransactionDate(journal.getTransactionDate());
        journalData.setTransactionId(journal.getTransactionId());
        journalData.setTransactionType(journal.getTransactionType());

        Set<Debit> debits = new HashSet<>();
        Set<Credit> credit = new HashSet<>();
        journal.getJournalEntries()
                .stream()
                .forEach(je -> {
                    if (je.isDebit()) {
                        debits.add(new Debit(je.getAccount().getAccountNumber(), String.valueOf(je.getDebit()),je.getDescription()));
                    } else {
                        credit.add(new Credit(je.getAccount().getAccountNumber(), String.valueOf(je.getCredit()),je.getDescription()));
                    }
                });
        journalData.setCredit(credit);
        journalData.setDebit(debits);
        return journalData;
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
        return accountRepository.findByAccountNumber(accountNo).get();
    }
    
    private void validateAccountForTransaction(final Account account) {
        /***
         * validate that the account allows manual adjustments and is not
         * disabled
         **/
        if (!account.getEnabled()) {
            throw  APIException.badRequest("Target account is not enabled");
        } 
    }
}

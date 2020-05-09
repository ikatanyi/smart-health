package io.smarthealth.debtor.claim.remittance.service;

import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.debtor.claim.remittance.data.RemitanceData;
import io.smarthealth.debtor.claim.remittance.domain.RemittanceOld;
import io.smarthealth.debtor.claim.remittance.domain.specification.RemitanceSpecification;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.bank.service.BankAccountService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.debtor.claim.remittance.domain.RemittanceOldRepository;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Deprecated
public class RemitanceService {

    private final RemittanceOldRepository remitanceRepository;
    private final PayerService payerService;
    private final BankAccountService bankAccountService;
    private final JournalService journalService;
    private final SequenceNumberService sequenceNumberService;

    @Transactional
    public RemittanceOld createRemitance(RemitanceData data) {
        //TODO move this transactions to receipt control account
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(data.getPayerId());

        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String receipt = sequenceNumberService.next(1L, Sequences.Receipt.name());
        RemittanceOld remitance = RemitanceData.map(data);
        remitance.setReceiptNo(receipt);
        remitance.setRemittanceNumber(data.getPaymentCode());
        remitance.setTransactionId(trdId);
        remitance.setPayer(payer);
        Optional<BankAccount> bank = bankAccountService.getBankAccount(data.getBankAccountId());
        if (bank.isPresent()) {
            remitance.setBankAccount(bank.get());
        }
        RemittanceOld savedRemitance = save(remitance);
        journalService.save(toJournal(savedRemitance));
//        journalEntryService.createJournalEntry(savedRemitance);
        return savedRemitance;
    }

    private RemittanceOld save(RemittanceOld remittance) {

        return remitanceRepository.save(remittance);
    }

    public RemittanceOld updateRemitance(final Long id, RemitanceData data) {
        RemittanceOld remitance = RemitanceData.map(data);
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(data.getPayerId());
        remitance.setAmount(data.getAmount());
        remitance.setBalance(data.getBalance());
//        remitance.setPaymentCode();
//        remitance.setReceiptNo();
        remitance.setTransactionId("");
        remitance.setPayer(payer);
        return remitanceRepository.save(remitance);
    }

    public RemittanceOld getRemitanceByIdWithFailDetection(Long id) {
        return remitanceRepository.findById(id).orElseThrow(() -> APIException.notFound("Remitance identified by id {0} not found ", id));
    }

    public Optional<RemittanceOld> getRemitance(Long id) {
        return remitanceRepository.findById(id);
    }

    public Page<RemittanceOld> getRemitances(Long payerId, Long bankId, Double balance, DateRange range, Pageable page) {
        Specification spec = RemitanceSpecification.createSpecification(payerId, bankId, balance, range);
        return remitanceRepository.findAll(spec, page);
    }

    public List<RemittanceOld> getAllRemitances() {
        return remitanceRepository.findAll();
    }

    private JournalEntry toJournal(RemittanceOld remittance) {
        if (remittance.getPayer().getDebitAccount() == null) {
            throw APIException.badRequest("Payer Ledger Account Not Mapped for {0} ", remittance.getPayer().getPayerName());
        }
        if (remittance.getBankAccount().getLedgerAccount() == null) {
            throw APIException.badRequest("Bank Ledger Account Not Mapped for {0} ", remittance.getBankAccount().getAccountName());
        }

        String debitAcc = remittance.getBankAccount().getLedgerAccount().getIdentifier();
        String creditAcc = remittance.getPayer().getDebitAccount().getIdentifier();
        BigDecimal amount = BigDecimal.valueOf(remittance.getAmount());
        String narration = "Payments for " + remittance.getPayer().getPayerName();
        JournalEntry toSave = new JournalEntry(remittance.getTransactionDate(), "Remittance Advice - " + remittance.getRemittanceNumber(),
                new JournalEntryItem[]{
                    new JournalEntryItem(remittance.getBankAccount().getLedgerAccount(), narration, amount, BigDecimal.ZERO),
                    new JournalEntryItem(remittance.getPayer().getDebitAccount(), narration, BigDecimal.ZERO,amount)
//                    new JournalEntryItem(narration, debitAcc, JournalEntryItem.Type.DEBIT, amount),
//                    new JournalEntryItem(narration, creditAcc, JournalEntryItem.Type.CREDIT, amount)
                }
        );
        toSave.setTransactionNo(remittance.getTransactionId());
        toSave.setTransactionType(TransactionType.Remittance);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }
}

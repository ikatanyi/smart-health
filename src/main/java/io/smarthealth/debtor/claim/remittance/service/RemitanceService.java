package io.smarthealth.debtor.claim.remittance.service;

import io.smarthealth.accounting.acc.service.JournalEntryService;
import io.smarthealth.debtor.claim.remittance.data.RemitanceData;
import io.smarthealth.debtor.claim.remittance.domain.Remittance;
import io.smarthealth.debtor.claim.remittance.domain.RemitanceRepository;
import io.smarthealth.debtor.claim.remittance.domain.specification.RemitanceSpecification;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.numbers.service.SequenceNumberGenerator;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.bank.service.BankAccountService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RemitanceService {

    private final RemitanceRepository remitanceRepository;
    private final PayerService payerService;
    private final BankAccountService bankAccountService;
    private final SequenceService seqService;
    private final JournalEntryService journalEntryService;
    private final SequenceNumberGenerator sequenceGenerator;
   

    @Transactional
    public Remittance createRemitance(RemitanceData data) { 
        Remittance remitance = RemitanceData.map(data); 
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(data.getPayerId());
        remitance.setRemittanceNumber(seqService.nextNumber(SequenceType.RemittanceNumber));
        remitance.setPayer(payer);
        Optional<BankAccount> bank = bankAccountService.getBankAccount(data.getBankAccountId());
        if (bank.isPresent()) {
            remitance.setBankAccount(bank.get());
        }
        Remittance savedRemitance= save(remitance);
        
        journalEntryService.createJournalEntry(savedRemitance);
        
        return savedRemitance;
    }

    private Remittance save(Remittance remittance) {
        String trdId = sequenceGenerator.generateTransactionNumber();
        remittance.setTransactionId(trdId);
        return remitanceRepository.save(remittance);
    }

    public Remittance updateRemitance(final Long id, RemitanceData data) {
        Remittance remitance = RemitanceData.map(data);
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(data.getPayerId());
        remitance.setAmount(data.getAmount());
        remitance.setBalance(data.getBalance());
//        remitance.setPaymentCode();
//        remitance.setReceiptNo();
        remitance.setTransactionId("");
        remitance.setPayer(payer);
        return remitanceRepository.save(remitance);
    }

    public Remittance getRemitanceByIdWithFailDetection(Long id) {
        return remitanceRepository.findById(id).orElseThrow(() -> APIException.notFound("Remitance identified by id {0} not found ", id));
    }

    public Optional<Remittance> getRemitance(Long id) {
        return remitanceRepository.findById(id);
    }

    public Page<Remittance> getRemitances(Long payerId, Long bankId, Double balance, DateRange range, Pageable page) {
        Specification spec = RemitanceSpecification.createSpecification(payerId, bankId, balance, range);
        return remitanceRepository.findAll(spec, page);
    }

    public List<Remittance> getAllRemitances() {
        return remitanceRepository.findAll();
    }
}

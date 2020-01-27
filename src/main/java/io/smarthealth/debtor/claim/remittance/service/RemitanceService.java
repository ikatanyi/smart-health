package io.smarthealth.debtor.claim.remittance.service;

import io.smarthealth.debtor.claim.remittance.data.RemitanceData;
import io.smarthealth.debtor.claim.remittance.domain.Remitance;
import io.smarthealth.debtor.claim.remittance.domain.RemitanceRepository;
import io.smarthealth.debtor.claim.remittance.domain.specification.RemitanceSpecification;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
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

        

    @javax.transaction.Transactional
    public Remitance createRemitance(RemitanceData data) {
        Remitance remitance = RemitanceData.map(data);
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(data.getPayerId());
        remitance.setPayer(payer);    
        BankAccount bank = bankAccountService.getBankAccountByIdWithFailDetection(data.getBankId());
        remitance.setBankAccount(bank);
        return remitanceRepository.save(remitance);
    }
    
    public Remitance updateRemitance(final Long id, RemitanceData data) {
        Remitance remitance = RemitanceData.map(data);
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(data.getPayerId());
        remitance.setAmount(data.getAmount());
        remitance.setBalance(data.getBalance());
//        remitance.setPaymentCode();
//        remitance.setReceiptNo();
        remitance.setTransactionId("");
        remitance.setPayer(payer);    
        return remitanceRepository.save(remitance);
    }

    public Remitance getRemitanceByIdWithFailDetection(Long id) {
        return remitanceRepository.findById(id).orElseThrow(() -> APIException.notFound("Remitance identified by id {0} not found ", id));
    }

    public Optional<Remitance> getRemitance(Long id) {
        return remitanceRepository.findById(id);
    }

    public Page<Remitance> getRemitances(Long payerId, Long bankId, Double balance, DateRange range, Pageable page) {
        Specification spec = RemitanceSpecification.createSpecification(payerId, bankId, balance, range);
        return remitanceRepository.findAll(spec, page);
    }

    public List<Remitance> getAllRemitances() {
        return remitanceRepository.findAll();
    }
}

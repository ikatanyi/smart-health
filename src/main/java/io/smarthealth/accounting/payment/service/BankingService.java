package io.smarthealth.accounting.payment.service;

import io.smarthealth.accounting.payment.data.BankChargeData;
import io.smarthealth.accounting.payment.data.BankingData;
import io.smarthealth.accounting.payment.data.InterbankData;
import io.smarthealth.accounting.payment.domain.Banking;
import io.smarthealth.accounting.payment.domain.BankingRepository;
import io.smarthealth.accounting.payment.domain.Payment;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.domain.enumeration.BankingType;
import io.smarthealth.accounting.payment.domain.specification.BankingSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.bank.domain.BankAccountRepository;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class BankingService {

    private final SequenceNumberService sequenceNumberService;
    private final BankAccountRepository bankAccountRepository;
    private final BankingRepository repository;

    public Optional<BankAccount> findBankAccountByNumber(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber);
    }

    public Banking save(Banking banking) {
        return repository.save(banking);
    }

    public List<Banking> save(List<Banking> banking) {
        return repository.saveAll(banking);
    }

    public Banking deposit(BankAccount bankAccount, Receipt receipt, BigDecimal amount) {
        Banking bank = new Banking();
        bank.setBankAccount(bankAccount);
        bank.setClient(receipt.getPayer());
        bank.setCredit(BigDecimal.ZERO);
        bank.setCurrency(receipt.getCurrency());
        bank.setDate(LocalDate.now());
        bank.setDebit(amount);
        bank.setDescription(receipt.getDescription());
        bank.setPaymentMode(receipt.getPaymentMethod());
        bank.setReferenceNumber(receipt.getReferenceNumber());
        String trnid = receipt.getTransactionNo() != null ? receipt.getTransactionNo() : sequenceNumberService.next(1L, Sequences.Transactions.name());
        bank.setTransactionNo(trnid);
        bank.setTransactionType(BankingType.Banking);

        return save(bank);
    }

    public Banking deposit(BankingData data) {

        BankAccount bankAccount = findBankAccountByNumber(data.getBankAccountNumber())
                .orElseThrow(() -> APIException.notFound("Bank Account Number {0} Not Found", data.getBankAccountNumber()));

        Banking bank = new Banking();
        bank.setBankAccount(bankAccount);
        bank.setClient(data.getClient());
        bank.setCredit(data.getCredit());
        bank.setCurrency(data.getCurrency());
        bank.setDate(data.getTransactionDate());
        bank.setDebit(data.getDebit());
        bank.setDescription(data.getDescription());
        bank.setPaymentMode(data.getPaymentMode());
        bank.setReferenceNumber(data.getReferenceNumber());
        String trnid = data.getTransactionNo() != null ? data.getTransactionNo() : sequenceNumberService.next(1L, Sequences.Transactions.name());
        bank.setTransactionNo(trnid);
        bank.setTransactionType(BankingType.Banking);

        return repository.save(bank);
    }

    public Banking withdraw(BankingData data) {
        BankAccount bankAccount = findBankAccountByNumber(data.getBankAccountNumber())
                .orElseThrow(() -> APIException.notFound("Bank Account Number {0} Not Found", data.getBankAccountNumber()));
        Banking bank = new Banking();
        bank.setBankAccount(bankAccount);
        bank.setClient(data.getClient());
        bank.setCredit(data.getCredit());
        bank.setCurrency(data.getCurrency());
        bank.setDate(data.getTransactionDate());
        bank.setDebit(data.getDebit());
        bank.setDescription(data.getDescription());
        bank.setPaymentMode(data.getPaymentMode());
        bank.setReferenceNumber(data.getReferenceNumber());
        String trnid = data.getTransactionNo() != null ? data.getTransactionNo() : sequenceNumberService.next(1L, Sequences.Transactions.name());
        bank.setTransactionNo(trnid);
        bank.setTransactionType(BankingType.Payment);

        return repository.save(bank);
    }

    public Banking withdraw(BankAccount bankAccount, Payment payment) {
        Banking bank = new Banking();
        bank.setBankAccount(bankAccount);
        bank.setClient(payment.getPayee());
        bank.setCredit(payment.getAmount());
        bank.setCurrency(payment.getCurrency());
        bank.setDate(LocalDate.now());
        bank.setDebit(BigDecimal.ZERO);
        bank.setDescription(payment.getDescription());
        bank.setPaymentMode(payment.getPaymentMethod());
        bank.setReferenceNumber(payment.getReferenceNumber());
        String trnid = payment.getTransactionNo() != null ? payment.getTransactionNo() : sequenceNumberService.next(1L, Sequences.Transactions.name());
        bank.setTransactionNo(trnid);
        bank.setTransactionType(BankingType.Payment);

        return save(bank);
    }
   public Banking bankingCharges(BankChargeData data){ 
       BankAccount bank = findBankAccountByNumber(data.getBankAccountNumber())
                .orElseThrow(() -> APIException.notFound("Bank Account Number {0} Not Found", data.getBankAccountNumber()));
        
        return bankingCharges(bank,data.getAmount(),sequenceNumberService.next(1L, Sequences.Transactions.name()));
    }
    public Banking bankingCharges(BankAccount bankAccount, BigDecimal amount, String transactionNo) {
        Banking bank = new Banking(bankAccount, LocalDate.now(), "", "Bank Charges", BigDecimal.ZERO, amount, "", "", BankingType.BankCharges, transactionNo, "");
        return save(bank);
    }

    public String transfer(InterbankData data) {
        BankAccount from = findBankAccountByNumber(data.getFromAccount())
                .orElseThrow(() -> APIException.notFound("Bank Account Number {0} Not Found", data.getFromAccount()));

        BankAccount to = findBankAccountByNumber(data.getToAccount())
                .orElseThrow(() -> APIException.notFound("Bank Account Number {0} Not Found", data.getToAccount()));

        String trnid = data.getTransactionNo() != null ? data.getTransactionNo() : sequenceNumberService.next(1L, Sequences.Transactions.name());
        List<Banking> list = new ArrayList<>();
        Banking fromBanking = new Banking();
        fromBanking.setBankAccount(from);
        fromBanking.setClient("Inter-banking");
        fromBanking.setCredit(data.getAmount());
        fromBanking.setCurrency("KES");
        fromBanking.setDate(LocalDate.now());
        fromBanking.setDebit(BigDecimal.ZERO);
        fromBanking.setDescription(data.getDescription());
        fromBanking.setPaymentMode("Cash");
        fromBanking.setReferenceNumber(data.getTransactionNo());
        fromBanking.setTransactionNo(trnid);
        fromBanking.setTransactionType(BankingType.InterBanking);
        list.add(fromBanking);

        Banking toBanking = new Banking();
        toBanking.setBankAccount(to);
        toBanking.setClient("Inter-banking");
        toBanking.setCredit(BigDecimal.ZERO);
        toBanking.setCurrency("KES");
        toBanking.setDate(LocalDate.now());
        toBanking.setDebit(data.getAmount());
        toBanking.setDescription(data.getDescription());
        toBanking.setPaymentMode("Cash");
        toBanking.setReferenceNumber(data.getTransactionNo());
        toBanking.setTransactionNo(trnid);
        toBanking.setTransactionType(BankingType.InterBanking);
        list.add(toBanking);

        repository.saveAll(list);
        return trnid;
    }

    public Banking getBanking(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Banking with Id {0} Not Found", id));
    }

    public BankAccount getBankAccount(String bankAccount) {
        return findBankAccountByNumber(bankAccount)
                .orElseThrow(() -> APIException.notFound("Bank Account Number {0} Not Found", bankAccount));
    }

    public Page<Banking> getBankings(String accountNumber, String client, String referenceNumber, String transactionNo, BankingType transactionType, DateRange range, Pageable page) {
        Specification<Banking> spec = BankingSpecification.createSpecification(accountNumber, client, referenceNumber, transactionNo, transactionType, range);
        return repository.findAll(spec, page);
    }
}

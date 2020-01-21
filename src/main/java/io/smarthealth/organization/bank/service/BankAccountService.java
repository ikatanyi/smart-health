package io.smarthealth.organization.bank.service;

import io.smarthealth.accounting.acc.domain.AccountEntity;
import io.smarthealth.accounting.acc.service.AccountService;
import io.smarthealth.appointment.domain.specification.BankAccountSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.bank.data.BankAccountData;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.bank.domain.BankAccountRepository;
import io.smarthealth.organization.bank.domain.enumeration.BankType;
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
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final AccountService accountService;

        

    @javax.transaction.Transactional
    public BankAccount createBankAccount(BankAccountData data) {
        BankAccount bankAccount = BankAccountData.map(data);
        Optional<AccountEntity> accEntity = accountService.findByAccountNumber(data.getAccountNumber());
        if(accEntity.isPresent())
            bankAccount.setAccount(accEntity.get());        
        return bankAccountRepository.save(bankAccount);
    }
    
    public BankAccount updateBankAccount(final Long id, BankAccountData data) {
        BankAccount bankAccount = getBankAccountByIdWithFailDetection(id);
        Optional<AccountEntity> accEntity = accountService.findByAccountNumber(data.getAccountNumber());
        if(accEntity.isPresent())
            bankAccount.setAccount(accEntity.get());    
        bankAccount.setAccountNumber(data.getAccountNumber());
        bankAccount.setBankBranch(data.getBankBranch());
        bankAccount.setBankName(data.getBankName());
        bankAccount.setBankType(data.getBankType());
        bankAccount.setCurrency(data.getCurrency());
        bankAccount.setDescription(data.getDescription());
        bankAccount.setIsDefault(data.getIsDefault());
        return bankAccountRepository.save(bankAccount);
    }

    public BankAccount getBankAccountByIdWithFailDetection(Long id) {
        return bankAccountRepository.findById(id).orElseThrow(() -> APIException.notFound("Bank Account identified by id {0} not found ", id));
    }

    public Optional<BankAccount> getBankAccount(Long id) {
        return bankAccountRepository.findById(id);
    }

    public Optional<BankAccount> getBankAccountByName(String name) {
        return bankAccountRepository.findByBankName(name);
    }

    public Page<BankAccount> getBankAccounts(String bankName, String bankBranch, BankType type, Pageable page) {
        Specification spec = BankAccountSpecification.createSpecification(bankName, bankBranch, type);
        return bankAccountRepository.findAll(spec, page);
    }

    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }
}

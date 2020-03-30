package io.smarthealth.organization.bank.service;

import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.service.AccountService;
import io.smarthealth.administration.banks.domain.BankBranch;
import io.smarthealth.administration.banks.domain.Bank;
import io.smarthealth.administration.banks.service.BankService;
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
import org.springframework.transaction.annotation.Transactional;

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
    private final BankService bankService;

    @Transactional
    public BankAccount createBankAccount(BankAccountData data) {
        BankAccount bank = new BankAccount();
        Optional<Account> accEntity = accountService.findByAccountNumber(data.getLedgerAccount());
        if (accEntity.isPresent()) {
            bank.setLedgerAccount(accEntity.get());
        }
        Bank mBank = bankService.fetchBankById(data.getBankId());
        bank.setBank(mBank);
        BankBranch branch = bankService.fetchBankBranchById(data.getBranchId());
        bank.setBankBranch(branch);

        bank.setAccountNumber(data.getAccountNumber());
        bank.setAccountName(data.getAccountName());
        bank.setIsDefault(data.getIsDefault());
        bank.setCurrency(data.getCurrency());
        bank.setDescription(data.getDescription());
        bank.setBankType(data.getBankType());

        return bankAccountRepository.save(bank);
    }

    public BankAccount updateBankAccount(final Long id, BankAccountData data) {
        BankAccount bankAccount = getBankAccountByIdWithFailDetection(id);
        Optional<Account> accEntity = accountService.findByAccountNumber(data.getLedgerAccount());
        if (accEntity.isPresent()) {
            bankAccount.setLedgerAccount(accEntity.get());
        }
        Bank bank = bankService.fetchBankById(data.getBankId());
        bankAccount.setBank(bank);
        BankBranch branch = bankService.fetchBankBranchById(data.getBranchId());
        bankAccount.setBankBranch(branch);

        bankAccount.setAccountNumber(data.getAccountNumber());
        bankAccount.setAccountName(data.getAccountName());
        bankAccount.setIsDefault(data.getIsDefault());
        bankAccount.setCurrency(data.getCurrency());
        bankAccount.setDescription(data.getDescription());

        bankAccount.setBankType(data.getBankType());
        return bankAccountRepository.save(bankAccount);
    }

    public BankAccount getBankAccountByIdWithFailDetection(Long id) {
        return bankAccountRepository.findById(id).orElseThrow(() -> APIException.notFound("Bank Account identified by id {0} not found ", id));
    }

    public Optional<BankAccount> getBankAccount(Long id) {
        return bankAccountRepository.findById(id);
    }

//    public Optional<BankAccount> getBankAccountByName(String name) {
//        return bankAccountRepository.findByBankName(name);
//    }
    public Page<BankAccount> getBankAccounts(String accountNumber,String bankName, String bankBranch, BankType type, Pageable page) {
        Specification spec = BankAccountSpecification.createSpecification(accountNumber,bankName, bankBranch, type);
        return bankAccountRepository.findAll(spec, page);
    }

    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }
}

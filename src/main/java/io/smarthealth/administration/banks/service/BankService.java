package io.smarthealth.administration.banks.service;

import io.smarthealth.administration.banks.domain.Bank;
import io.smarthealth.administration.banks.domain.BankBranch;
import io.smarthealth.administration.banks.domain.BankBranchRepository;
import io.smarthealth.administration.banks.domain.BankRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final BankBranchRepository bankBranchRepository;

    @Transactional
    public Bank createBank(Bank mainBank) {
        return bankRepository.save(mainBank);
    }
    
    public Bank updateBank(Long id, Bank mainBank) {
        Bank bank = fetchBankById(id);
        bank.setBankBranch(mainBank.getBankBranch());
        bank.setBankCode(mainBank.getBankCode());
        bank.setBankName(mainBank.getBankName());
        bank.setBankShortName(mainBank.getBankShortName());
        return bankRepository.save(bank);
    }

    public Page<Bank> fetchAllMainBanks(Pageable pgbl) {
        return bankRepository.findAll(pgbl);
    }

    @Transactional
    public List<BankBranch> createBankBranch(List<BankBranch> branch) {
        return bankBranchRepository.saveAll(branch);
    }

    public Optional<Bank> fetchBankByName(String bankName) {
        return bankRepository.findByBankName(bankName);
    }

    public Optional<BankBranch> findByBranchNameAndBank(final String branchName, final Bank mainBank) {
        return bankBranchRepository.findByBranchNameAndBank(branchName, mainBank);
    }
    
     public List<Bank> searchBankByNameOrShortName(final String name) {
        return bankRepository.findByBankNameContainingIgnoreCaseOrBankShortNameContainingIgnoreCase(name, name);
    }

    public Page<BankBranch> fetchBranchByMainBank(Bank mb, Pageable pageable) {
        return bankBranchRepository.findByBank(mb, pageable);
    }

    public Page<BankBranch> fetchAllBranchsInBank(Pageable pageable) {
        return bankBranchRepository.findAll(pageable);
    }

    public Bank fetchBankById(Long id) {
        return bankRepository.findById(id).orElseThrow(() -> APIException.notFound("Bank identified by {0} was not found", id));
    }

    public BankBranch fetchBankBranchById(Long id) {
        return bankBranchRepository.findById(id).orElseThrow(() -> APIException.notFound("Bank branch identified by {0} was not found", id));
    }

    public BankBranch fetchBankBranchByCode(String code) {
        return bankBranchRepository.findByBranchCode(code).orElseThrow(() -> APIException.notFound("Bank branch identified by code {0} was not found", code));
    }
}

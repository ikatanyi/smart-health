/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.bank.service;

import io.smarthealth.accounting.account.domain.AccountRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.ContentPage;
import io.smarthealth.organization.bank.data.BankAccountData;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.bank.domain.BankAccountRepository;
import io.smarthealth.organization.domain.Organization;
import io.smarthealth.organization.domain.OrganizationRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankService {

    @Autowired
    BankAccountRepository bankAccountRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    @Transactional
    public Long createBank(final BankAccountData bankData) {
        BankAccount bank = BankAccountData.map(bankData);

        bank.setGlAccount(accountRepository.findByAccountNumber(bankData.getGlAccountCode()).get());
        bank.setOrganization(organizationRepository.findByCode(bankData.getOrganizationCode()).get());

        bankAccountRepository.save(bank);

        return bank.getId();
    }

    public BankAccountData fetchBankById(final Long bankId) {
        try {
            BankAccount bank = bankAccountRepository.findById(bankId).get();
            return BankAccountData.map(bank);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("An error occured when fetching bank by Id {0}", bankId);
        }
    }

    public Long updateBankAccount(final Long bankId, BankAccountData bankAccountData) {
        try {
            BankAccount bank = bankAccountRepository.findById(bankId).get();
            bank.setAccountName(bankAccountData.getAccountName());
            bank.setAccountNo(bankAccountData.getAccountNo());
            bank.setBank(bankAccountData.getBank());
            bank.setBranchCode(bankAccountData.getBranchCode());
            bank.setCompanyAccount(bankAccountData.getCompanyAccount());
            bank.setDefaultAccount(bankAccountData.getDefaultAccount()); 
            bank.setIBAN(bankAccountData.getIBAN());
            bank.setOrganization(organizationRepository.findByCode(bankAccountData.getGlAccountCode()).get());
            bank.setSwiftNumber(bankAccountData.getSwiftNumber());
            return bankId;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("An error occured while updating bank id {0}", bankId);
        }
    }

    public ContentPage<BankAccountData> fetchAllBankAccounts(final Pageable pageable) {
        try {
            Page<BankAccount> banks = bankAccountRepository.findAll(pageable);
            final ContentPage<BankAccountData> bankAccountPage = new ContentPage();
            bankAccountPage.setTotalElements(banks.getTotalElements());
            bankAccountPage.setTotalPages(banks.getTotalPages());
            if (banks.getSize() > 0) {
                List<BankAccountData> banksData = new ArrayList<>();
                for (BankAccount bank : banks.getContent()) {
                    BankAccountData bankData = BankAccountData.map(bank);
                    banksData.add(bankData);
                }
                bankAccountPage.setContents(banksData);
            }
            return bankAccountPage;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("An error while fetching all bank accounts", e.getMessage());
        }
    }

    public ContentPage<BankAccountData> fetchAllBankAccountsByOrganization(final String OrganizationCode, final Pageable pageable) {
        try {
            Organization org = organizationRepository.findByCode(OrganizationCode).get();
            Page<BankAccount> banks = bankAccountRepository.findByOrganization(org, pageable);
            final ContentPage<BankAccountData> bankAccountPage = new ContentPage();
            bankAccountPage.setTotalElements(banks.getTotalElements());
            bankAccountPage.setTotalPages(banks.getTotalPages());
            if (banks.getSize() > 0) {
                List<BankAccountData> banksData = new ArrayList<>();
                for (BankAccount bank : banks.getContent()) {
                    BankAccountData bankData = BankAccountData.map(bank);
                    banksData.add(bankData);
                }
                bankAccountPage.setContents(banksData);
            }

            return bankAccountPage;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("An error while fetching all bank accounts", e.getMessage());
        }
    }

}

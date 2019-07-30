/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.bank.data;

import io.smarthealth.organization.bank.domain.BankAccount;
import lombok.Data;

/**
 *
 * @author simon.waweru
 */
@Data
public class BankAccountData {

    private String organizationCode;
    private String organizationName;
    private String organizationId;

    private String accountName;
    private String accountNo;
    private String glAccountName;
    private String glAccountCode;
    private String bank;
    private String branchCode;
    private String swiftNumber;
    private String IBAN;
    private Boolean defaultAccount; // state if this is an organization default account
    private Boolean companyAccount; // link to the company

    public static BankAccountData map(final BankAccount bank) {
        BankAccountData bankData = new BankAccountData();
        bankData.setAccountName(bank.getAccountName());
        bankData.setAccountNo(bank.getAccountNo());
        bankData.setBank(bank.getBank());
        bankData.setBranchCode(bank.getBranchCode());
        bankData.setCompanyAccount(bank.getCompanyAccount());
        bankData.setDefaultAccount(bank.getDefaultAccount());
        bankData.setGlAccountName(bank.getGlAccount().getName());
        bankData.setGlAccountCode(bank.getGlAccount().getCode());
        bankData.setIBAN(bank.getIBAN());
        bankData.setOrganizationCode(bank.getOrganization().getCode());
        bankData.setOrganizationId(bank.getOrganization().getCompanyId());
        bankData.setOrganizationName(bank.getOrganization().getName());
        return bankData;
    }

    public static BankAccount map(BankAccountData bankAccountData) {
        BankAccount bank = new BankAccount();
        bank.setAccountName(bankAccountData.getAccountName());
        bank.setAccountNo(bankAccountData.getAccountNo());
        bank.setBank(bankAccountData.getBank());
        bank.setBranchCode(bankAccountData.getBranchCode());
        bank.setCompanyAccount(bankAccountData.getCompanyAccount());
        bank.setDefaultAccount(bankAccountData.getDefaultAccount());
        bank.getGlAccount().setCode(bankAccountData.getGlAccountCode());
        bank.setIBAN(bankAccountData.getIBAN());
        bank.getOrganization().setCode(bankAccountData.getOrganizationCode());
        return bank;
    }
}

package io.smarthealth.organization.org.data;

import io.smarthealth.administration.app.domain.BankAccount;
import io.smarthealth.organization.org.domain.OrganizationBank;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class OrganizationBankData {

    private Long id;
    private String accountName;
    private String accountNumber;
    private String bankName;
    private String bankBranch;
    private String swiftNumber;
    private Long ledgerId;
    private String ledgerAccount;
    private Boolean defaultAccount;

    public static OrganizationBankData map(OrganizationBank account) {
        OrganizationBankData data = new OrganizationBankData();
        data.setId(account.getId());
        if (account.getBank() != null) {
            BankAccount bank = account.getBank();
            data.setAccountName(bank.getAccountName());
            data.setAccountNumber(bank.getAccountNumber());
            data.setBankName(bank.getBankName());
            data.setBankBranch(bank.getBankBranch());
        }
        data.setSwiftNumber(account.getBank().getSwiftNumber());
        if (account.getLedgerAccount() != null) {
            data.setLedgerId(account.getLedgerAccount().getId());
            data.setLedgerAccount(account.getLedgerAccount().getAccountName());
        }
        data.setDefaultAccount(account.getDefaultAccount());

        return data;
    }

    public static OrganizationBank map(OrganizationBankData bankData) {
        OrganizationBank account = new OrganizationBank();
        if (bankData.getId() != null) {
            account.setId(bankData.getId());
        }
        BankAccount bank = new BankAccount();

        bank.setAccountName(bankData.getAccountName());
        bank.setAccountNumber(bankData.getAccountNumber());
        bank.setBankName(bankData.getBankName());
        bank.setBankBranch(bankData.getBankBranch());
        bank.setSwiftNumber(bankData.getSwiftNumber());
        account.setBank(bank);
        account.setDefaultAccount(bankData.getDefaultAccount());

        return account;
    }
}

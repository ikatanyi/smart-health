package io.smarthealth.organization.bank.data;

import io.smarthealth.accounting.acc.data.v1.AccountData;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.bank.domain.enumeration.BankType;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class BAccountData {

    private Long id;
    private String accountName;
    private String accountNumber;
    private String accountType;
    private String bankName;
    private String bankAccountNumber;
    private String currency;
    private String bankBranch;
    private String description;
    @Enumerated(EnumType.STRING)
    private BankType bankType;
    private Boolean isDefault;
    
    public static BankAccount map(BAccountData data){
        BankAccount bank = new BankAccount();
        bank.setBankName(data.getAccountNumber());
        bank.setAccountNumber(data.getAccountNumber());
        bank.setBankBranch(data.getBankBranch());
        bank.setIsDefault(data.getIsDefault());
        bank.setDescription(data.getDescription());
        return bank;
    }
    
    public static BAccountData map(BankAccount data){
        BAccountData bank = new BAccountData();
        bank.setBankName(data.getAccountNumber());
        bank.setBankAccountNumber(data.getAccountNumber());
        bank.setBankBranch(data.getBankBranch());
        bank.setIsDefault(data.getIsDefault());
        bank.setCurrency(data.getCurrency());
        bank.setDescription(data.getDescription());
        if(data.getAccount()!=null){
            bank.setAccountName(data.getAccount().getName());
            bank.setAccountNumber(data.getAccount().getIdentifier());
            bank.setAccountType(data.getAccount().getType());
        }
        
        bank.setDescription(data.getDescription());
        return bank;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.data;

import io.smarthealth.accounting.account.domain.AccountType;
import io.smarthealth.accounting.account.domain.enumeration.AccountCategory;
import lombok.Data;

/**
 *
 * @author Kelsas
 */ 
@Data 
public class AccountTypeData{
    private Long id; 
    private AccountCategory accountCategory;
    private String type;
    private String description;
    
    public static AccountTypeData map(AccountType accountType){
        AccountTypeData data=new AccountTypeData();
        data.setId(accountType.getId());
        data.setAccountCategory(accountType.getGlAccountType());
        data.setType(accountType.getType());
        data.setDescription(accountType.getDescription());
        return data;
    }
}

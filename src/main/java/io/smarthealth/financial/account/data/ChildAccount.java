/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.data;

import io.smarthealth.financial.account.domain.enumeration.AccountType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

/**
 *
 * @author Kelsas
 */
@Data
public class ChildAccount {

    private String accountCode;
    private String accountName;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    public ChildAccount() {
    }

    public ChildAccount(String accountCode, String accountName, AccountType accountType) {
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.accountType = accountType;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.data;
 
import javax.persistence.EnumType;
import javax.persistence.Enumerated; 
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
    private String accountType;

    public ChildAccount() {
    }

    public ChildAccount(String accountCode, String accountName, String accountType) {
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.accountType = accountType;
    }

}

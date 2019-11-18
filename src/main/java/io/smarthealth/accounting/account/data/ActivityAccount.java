/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.data;

import io.smarthealth.accounting.account.domain.FinancialActivityAccount;
import io.smarthealth.accounting.account.domain.enumeration.FinancialActivity;
import lombok.Value;

/**
 *
 * @author Kelsas
 */
@Value
public class ActivityAccount {
    private Long id;
    private FinancialActivity activity;
    private String activityName;
    private String accountName;
    private String accountIdentifier;

    public static ActivityAccount map(FinancialActivityAccount account) {
        return new ActivityAccount(
                account.getId(),
                account.getFinancialActivity(),
                account.getFinancialActivity().getActivityName(),
                account.getAccount().getAccountName(),
                account.getAccount().getAccountNumber()
        );
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.data;

import io.smarthealth.financial.account.domain.enumeration.AccountType;
import lombok.Data;
import lombok.Value;

/**
 *
 * @author Kelsas
 */
@Value
public class FinancialActivityData {

    private final Integer id;
    private final String name;
    private final AccountType mappedAccountType;

}

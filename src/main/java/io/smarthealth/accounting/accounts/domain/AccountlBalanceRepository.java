/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.accounts.domain;

import io.smarthealth.accounting.accounts.data.financial.statement.TransactionList;
import io.smarthealth.infrastructure.lang.DateRange;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author Kelsas
 */
public interface AccountlBalanceRepository {

    public BigDecimal getAccountsBalance(String accountNumber, DateRange period);
    
    public BigDecimal getAccountsBalance(String accountNumber, LocalDate date);
    
//    public AccountTransactions getAccountTransactions(String accountNumber, DateRange period);
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.accounts.data.financial.statement;

import io.smarthealth.accounting.accounts.data.AccountBalance;
import io.smarthealth.accounting.accounts.data.JournalEntryItemData;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class TransactionList {

    public List<JournalEntryItemData> transactions = new ArrayList<>();
    private String period;
}

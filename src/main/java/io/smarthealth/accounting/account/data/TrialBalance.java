/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.data;

import io.smarthealth.accounting.account.domain.TrialBalanceEntry;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class TrialBalance {

    private List<TrialBalanceEntry> trialBalanceEntries;
    private BigDecimal debitTotal;
    private BigDecimal creditTotal;

    public TrialBalance() {
        super();
        trialBalanceEntries=new ArrayList<>();
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.accounts.data;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class LedgerGrouping {

    String group;
    List<LedgerData> accountTypes;

    public LedgerGrouping(String group, List<LedgerData> accountTypes) {
        this.group = group;
        this.accountTypes = accountTypes != null ? accountTypes : new ArrayList<>();
    }

}

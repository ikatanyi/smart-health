/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class IncomeStatementSection {

    public enum Type {
        INCOME,
        EXPENSES
    }

    @NotEmpty
    private Type type;
    @NotEmpty
    private String description;
    @NotEmpty
    private List<IncomeStatementEntry> incomeStatementEntries = new ArrayList<>();
    @NotNull
    private BigDecimal subtotal = BigDecimal.ZERO;

    public void add(final IncomeStatementEntry incomeStatementEntry) {
        this.incomeStatementEntries.add(incomeStatementEntry);
        this.subtotal = this.subtotal.add(incomeStatementEntry.getValue());
    }
}

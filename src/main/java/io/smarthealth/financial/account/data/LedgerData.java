/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.data;

import io.smarthealth.financial.account.domain.Ledger;
import io.smarthealth.financial.account.domain.enumeration.AccountType;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class LedgerData {

    @NotNull
    private AccountType type;
    private String identifier;
    @NotEmpty
    private String name;
    private String description;
    private String parentLedgerIdentifier;
    @Valid
    private List<LedgerData> subLedgers;
    private BigDecimal totalValue; 
    @NotNull
    private Boolean showAccountsInChart;
}

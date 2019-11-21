/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.service;

import io.smarthealth.accounting.account.data.IncomeStatement;
import io.smarthealth.accounting.account.data.IncomeStatementEntry;
import io.smarthealth.accounting.account.data.IncomeStatementSection;
import io.smarthealth.accounting.account.domain.AccountRepository;
import io.smarthealth.accounting.account.domain.AccountType; 
import io.smarthealth.accounting.account.domain.enumeration.AccountCategory;
import java.math.BigDecimal; 
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class IncomeStatementService {

    private final AccountRepository ledgerRepository;

    public IncomeStatementService(final AccountRepository ledgerRepository) {
        super();
        this.ledgerRepository = ledgerRepository;
    }

    public IncomeStatement getIncomeStatement() {
        final IncomeStatement incomeStatement = new IncomeStatement();
        incomeStatement.setDate(LocalDateTime.now());

        this.createIncomeStatementSection(incomeStatement, AccountCategory.INCOME, IncomeStatementSection.Type.INCOME);
        this.createIncomeStatementSection(incomeStatement, AccountCategory.EXPENSE, IncomeStatementSection.Type.EXPENSES);

        incomeStatement.setGrossProfit(this.calculateTotal(incomeStatement, IncomeStatementSection.Type.INCOME));
        incomeStatement.setTotalExpenses(this.calculateTotal(incomeStatement, IncomeStatementSection.Type.EXPENSES));
        incomeStatement.setNetIncome(incomeStatement.getGrossProfit().subtract(incomeStatement.getTotalExpenses()));

        return incomeStatement;
    }

    private void createIncomeStatementSection(final IncomeStatement incomeStatement, final AccountCategory accountCategory,
            final IncomeStatementSection.Type incomeStatementType) {
            this.ledgerRepository.findParentAccountIsNullAndAccountCategory(accountCategory).forEach(ledgerEntity -> {
            final IncomeStatementSection incomeStatementSection = new IncomeStatementSection();
            incomeStatementSection.setType(IncomeStatementSection.Type.valueOf(incomeStatementType.name()));
            incomeStatementSection.setDescription(ledgerEntity.getAccountName());
            incomeStatement.add(incomeStatementSection);

            this.ledgerRepository.findByParentAccountOrderByAccountNumber(ledgerEntity).forEach(subLedgerEntity -> {
                final IncomeStatementEntry incomeStatementEntry = new IncomeStatementEntry();
                incomeStatementEntry.setDescription(subLedgerEntity.getAccountName());
                //this should pull the balances from the 
                //TODO
                final BigDecimal totalValue = BigDecimal.ZERO;;//subLedgerEntity.getTotalValue() != null ? subLedgerEntity.getTotalValue() : BigDecimal.ZERO;
                incomeStatementEntry.setValue(totalValue);
                incomeStatementSection.add(incomeStatementEntry);
                
            });
        });
    }

    private BigDecimal calculateTotal(final IncomeStatement incomeStatement, final IncomeStatementSection.Type incomeStatementType) {
        return incomeStatement.getIncomeStatementSections()
                .stream()
                .filter(incomeStatementSection
                        -> incomeStatementSection.getType().equals(incomeStatementType.name()))
                .map(IncomeStatementSection::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

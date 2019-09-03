/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.service;

import io.smarthealth.financial.account.data.IncomeStatement;
import io.smarthealth.financial.account.data.IncomeStatementEntry;
import io.smarthealth.financial.account.data.IncomeStatementSection;
import io.smarthealth.financial.account.domain.enumeration.AccountType;
import io.smarthealth.financial.account.domain.LedgerRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class IncomeStatementService {

    private final LedgerRepository ledgerRepository;

    public IncomeStatementService(final LedgerRepository ledgerRepository) {
        super();
        this.ledgerRepository = ledgerRepository;
    }

    public IncomeStatement getIncomeStatement() {
        final IncomeStatement incomeStatement = new IncomeStatement();
        incomeStatement.setDate(LocalDateTime.now());

        this.createIncomeStatementSection(incomeStatement, AccountType.REVENUE, IncomeStatementSection.Type.INCOME);
        this.createIncomeStatementSection(incomeStatement, AccountType.EXPENSE, IncomeStatementSection.Type.EXPENSES);

        incomeStatement.setGrossProfit(this.calculateTotal(incomeStatement, IncomeStatementSection.Type.INCOME));
        incomeStatement.setTotalExpenses(this.calculateTotal(incomeStatement, IncomeStatementSection.Type.EXPENSES));
        incomeStatement.setNetIncome(incomeStatement.getGrossProfit().subtract(incomeStatement.getTotalExpenses()));

        return incomeStatement;
    }

    private void createIncomeStatementSection(final IncomeStatement incomeStatement, final AccountType accountType,
            final IncomeStatementSection.Type incomeStatementType) {
        this.ledgerRepository.findByParentLedgerIsNullAndType(accountType.name()).forEach(ledgerEntity -> {
            final IncomeStatementSection incomeStatementSection = new IncomeStatementSection();
            incomeStatementSection.setType(IncomeStatementSection.Type.valueOf(incomeStatementType.name()));
            incomeStatementSection.setDescription(ledgerEntity.getName());
            incomeStatement.add(incomeStatementSection);

            this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity).forEach(subLedgerEntity -> {
                final IncomeStatementEntry incomeStatementEntry = new IncomeStatementEntry();
                incomeStatementEntry.setDescription(subLedgerEntity.getName());
                final BigDecimal totalValue = subLedgerEntity.getTotalValue() != null ? subLedgerEntity.getTotalValue() : BigDecimal.ZERO;
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

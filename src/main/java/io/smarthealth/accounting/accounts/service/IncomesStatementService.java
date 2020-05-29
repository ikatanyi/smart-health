package io.smarthealth.accounting.accounts.service;

import io.smarthealth.accounting.accounts.data.financial.statement.IncomeStatement;
import io.smarthealth.accounting.accounts.data.financial.statement.IncomeStatementEntry;
import io.smarthealth.accounting.accounts.data.financial.statement.IncomeStatementSection;
import io.smarthealth.accounting.accounts.domain.AccountType;
import io.smarthealth.accounting.accounts.domain.LedgerRepository;
import io.smarthealth.infrastructure.lang.DateConverter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomesStatementService {

    private final LedgerRepository ledgerRepository;

    public IncomeStatement getIncomeStatement() {
        final IncomeStatement incomeStatement = new IncomeStatement();
        incomeStatement.setDate(DateConverter.toIsoString(LocalDateTime.now()));

        this.createIncomeStatementSection(incomeStatement, AccountType.REVENUE, IncomeStatementSection.Type.INCOME);
        this.createIncomeStatementSection(incomeStatement, AccountType.EXPENSE, IncomeStatementSection.Type.EXPENSES);

        incomeStatement.setGrossProfit(this.calculateTotal(incomeStatement, IncomeStatementSection.Type.INCOME));
        incomeStatement.setTotalExpenses(this.calculateTotal(incomeStatement, IncomeStatementSection.Type.EXPENSES));
        incomeStatement.setNetIncome(incomeStatement.getGrossProfit().subtract(incomeStatement.getTotalExpenses()));

        return incomeStatement;
    }

    private void createIncomeStatementSection(final IncomeStatement incomeStatement, final AccountType accountType,
            final IncomeStatementSection.Type incomeStatementType) {

        this.ledgerRepository.findByParentLedgerIsNullAndAccountType(accountType)
                .forEach(ledgerEntity -> {
                    final IncomeStatementSection incomeStatementSection = new IncomeStatementSection();
                    incomeStatementSection.setType(incomeStatementType.name());
                    incomeStatementSection.setDescription(ledgerEntity.getName());
                    incomeStatement.add(incomeStatementSection);

                    this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity)
                            .forEach(subLedgerEntity -> {
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

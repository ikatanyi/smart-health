package io.smarthealth.accounting.acc.service;

import io.smarthealth.accounting.acc.data.v1.AccountType;
import io.smarthealth.accounting.acc.data.v1.financial.statement.IncomeStatement;
import io.smarthealth.accounting.acc.data.v1.financial.statement.IncomeStatementEntry;
import io.smarthealth.accounting.acc.data.v1.financial.statement.IncomeStatementSection;
import io.smarthealth.accounting.acc.domain.LedgerRepository;
import io.smarthealth.accounting.acc.validation.DateConverter;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IncomesStatementService {

    private final LedgerRepository ledgerRepository;

    @Autowired
    public IncomesStatementService(final LedgerRepository ledgerRepository) {
        super();
        this.ledgerRepository = ledgerRepository;
    }

    public IncomeStatement getIncomeStatement() {
        final IncomeStatement incomeStatement = new IncomeStatement();
        incomeStatement.setDate(DateConverter.toIsoString(LocalDateTime.now(Clock.systemUTC())));

        this.createIncomeStatementSection(incomeStatement, AccountType.REVENUE, IncomeStatementSection.Type.INCOME);
        this.createIncomeStatementSection(incomeStatement, AccountType.EXPENSE, IncomeStatementSection.Type.EXPENSES);

        incomeStatement.setGrossProfit(this.calculateTotal(incomeStatement, IncomeStatementSection.Type.INCOME));
        incomeStatement.setTotalExpenses(this.calculateTotal(incomeStatement, IncomeStatementSection.Type.EXPENSES));
        incomeStatement.setNetIncome(incomeStatement.getGrossProfit().subtract(incomeStatement.getTotalExpenses()));

        return incomeStatement;
    }

    private void createIncomeStatementSection(final IncomeStatement incomeStatement, final AccountType accountType,
            final IncomeStatementSection.Type incomeStatementType) {

        this.ledgerRepository.findByParentLedgerIsNullAndType(accountType.name())
                .forEach(ledgerEntity -> {
                    System.err.println("this is the ledger"+ledgerEntity.getName());
                    final IncomeStatementSection incomeStatementSection = new IncomeStatementSection();
                    incomeStatementSection.setType(incomeStatementType.name());
                    incomeStatementSection.setDescription(ledgerEntity.getName());
                    incomeStatement.add(incomeStatementSection);

                    this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity)
                            .forEach(subLedgerEntity -> {
                                System.err.println("Found a ledger ... "+subLedgerEntity);
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

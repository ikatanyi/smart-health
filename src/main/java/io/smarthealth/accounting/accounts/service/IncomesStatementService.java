package io.smarthealth.accounting.accounts.service;

import io.smarthealth.accounting.accounts.data.financial.statement.IncomeStatement;
import io.smarthealth.accounting.accounts.data.financial.statement.IncomeStatementEntry;
import io.smarthealth.accounting.accounts.data.financial.statement.IncomeStatementSection;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.AccountRepository;
import io.smarthealth.accounting.accounts.domain.AccountType;
import io.smarthealth.accounting.accounts.domain.JournalEntryItemRepository;
import io.smarthealth.accounting.accounts.domain.Ledger;
import io.smarthealth.accounting.accounts.domain.LedgerRepository;
import io.smarthealth.infrastructure.lang.DateConverter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomesStatementService {

    private final LedgerRepository ledgerRepository;
    private final JournalEntryItemRepository journalEntryItemRepository;
    private final AccountRepository accountRepository;

    public IncomeStatement getIncomeStatement(LocalDate asAt) {
        final IncomeStatement incomeStatement = new IncomeStatement();
        if (asAt != null) {
            incomeStatement.setDate(DateConverter.toIsoString(asAt));
            incomeStatement.setAsAt(asAt);

        } else {
            incomeStatement.setDate(DateConverter.toIsoString(LocalDateTime.now()));
        }
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
                                BigDecimal totalValue = subLedgerEntity.getTotalValue() != null ? subLedgerEntity.getTotalValue() : BigDecimal.ZERO; // this is one needs
                                if (incomeStatement.getAsAt() != null) {
                                    totalValue = calculateTotalAsAt(subLedgerEntity, incomeStatement.getAsAt());
                                    subLedgerEntity.setTotalValue(totalValue);
                                }
                               
                                incomeStatementEntry.setValue(totalValue);
                                incomeStatementSection.add(incomeStatementEntry);
                            });
                });
    }

     private BigDecimal calculateTotalAsAt(Ledger ledger, LocalDate asAt) {
        List<Account> accounts = accountRepository.findByLedger(ledger);
        return accounts
                .stream()
                .map(x -> {
                    BigDecimal amt = journalEntryItemRepository.getAccountsBalance(x.getIdentifier(), asAt);
                    if (amt == null) {
                        return BigDecimal.ZERO;
                    }
                    return amt;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    private BigDecimal calculateTotal(final IncomeStatement incomeStatement, final IncomeStatementSection.Type incomeStatementType) {
        return incomeStatement.getIncomeStatementSections()
                .stream()
                .filter(incomeStatementSection
                        -> incomeStatementSection.getType().equals(incomeStatementType.name()))
                .map(IncomeStatementSection::getSubtotal) // this  now I need to go to journal and calculate balances
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}

package io.smarthealth.accounting.accounts.service;

import io.smarthealth.accounting.accounts.data.financial.statement.FinancialCondition;
import io.smarthealth.accounting.accounts.data.financial.statement.FinancialConditionEntry;
import io.smarthealth.accounting.accounts.data.financial.statement.FinancialConditionSection;
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
import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialConditionService {

    private final LedgerRepository ledgerRepository;
    private final JournalEntryItemRepository journalEntryItemRepository;
    private final AccountRepository accountRepository;

    public FinancialCondition getFinancialCondition(LocalDate localDate) {
        final FinancialCondition financialCondition = new FinancialCondition();
        if (localDate != null) {
            financialCondition.setDate(DateConverter.toIsoString(localDate));
        } else {
            financialCondition.setDate(DateConverter.toIsoString(LocalDateTime.now()));
        }
        this.createFinancialConditionSection(financialCondition, AccountType.ASSET, FinancialConditionSection.Type.ASSET);
        this.createFinancialConditionSection(financialCondition, AccountType.EQUITY, FinancialConditionSection.Type.EQUITY);
        this.createFinancialConditionSection(financialCondition, AccountType.LIABILITY, FinancialConditionSection.Type.LIABILITY);

        financialCondition.setTotalAssets(
                this.calculateTotal(financialCondition,
                        EnumSet.of(FinancialConditionSection.Type.ASSET))
        );
        financialCondition.setTotalEquitiesAndLiabilities(
                this.calculateTotal(financialCondition,
                        EnumSet.of(FinancialConditionSection.Type.EQUITY, FinancialConditionSection.Type.LIABILITY))
        );

        return financialCondition;
    }

    private void createFinancialConditionSection(final FinancialCondition financialCondition, final AccountType accountType,
            final FinancialConditionSection.Type financialConditionType) {
        this.ledgerRepository.findByParentLedgerIsNullAndAccountType(accountType)
                .forEach(ledgerEntity -> {
                    final FinancialConditionSection financialConditionSection = new FinancialConditionSection();
                    financialConditionSection.setType(financialConditionType.name());
                    financialConditionSection.setDescription(ledgerEntity.getName());
                    financialCondition.add(financialConditionSection);

                    this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity)
                            .forEach(subLedgerEntity -> {
                                final FinancialConditionEntry financialConditionEntry = new FinancialConditionEntry();
                                financialConditionEntry.setDescription(subLedgerEntity.getName());
                                BigDecimal totalValue = subLedgerEntity.getTotalValue() != null ? subLedgerEntity.getTotalValue() : BigDecimal.ZERO;

                                if (financialCondition.getAsAt() != null) {
                                    totalValue = calculateTotalAsAt(subLedgerEntity, financialCondition.getAsAt());
                                }

                                financialConditionEntry.setValue(totalValue);
                                financialConditionSection.add(financialConditionEntry);
                            });
                });
    }

    private BigDecimal calculateTotal(final FinancialCondition financialCondition,
            final EnumSet<FinancialConditionSection.Type> financialConditionTypes) {
        return financialCondition.getFinancialConditionSections()
                .stream()
                .filter(financialConditionSection
                        -> financialConditionTypes.contains(FinancialConditionSection.Type.valueOf(financialConditionSection.getType())))
                .map(FinancialConditionSection::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
}

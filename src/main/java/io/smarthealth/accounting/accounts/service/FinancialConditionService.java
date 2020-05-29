package io.smarthealth.accounting.accounts.service;

import io.smarthealth.accounting.accounts.data.financial.statement.FinancialCondition;
import io.smarthealth.accounting.accounts.data.financial.statement.FinancialConditionEntry;
import io.smarthealth.accounting.accounts.data.financial.statement.FinancialConditionSection;
import io.smarthealth.accounting.accounts.domain.AccountType;
import io.smarthealth.accounting.accounts.domain.LedgerRepository;
import io.smarthealth.infrastructure.lang.DateConverter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialConditionService {

    private final LedgerRepository ledgerRepository;
 
    public FinancialCondition getFinancialCondition() {
        final FinancialCondition financialCondition = new FinancialCondition();
        financialCondition.setDate(DateConverter.toIsoString(LocalDateTime.now()));

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
                final BigDecimal totalValue = subLedgerEntity.getTotalValue() != null ? subLedgerEntity.getTotalValue() : BigDecimal.ZERO;
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
}

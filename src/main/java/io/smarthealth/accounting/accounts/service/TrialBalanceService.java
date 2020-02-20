package io.smarthealth.accounting.accounts.service;

import io.smarthealth.accounting.accounts.data.LedgerData;
import io.smarthealth.accounting.accounts.data.financial.statement.TrialBalance;
import io.smarthealth.accounting.accounts.data.financial.statement.TrialBalanceEntry;
import io.smarthealth.accounting.accounts.domain.LedgerRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import org.springframework.stereotype.Service;

@Service
public class TrialBalanceService {

    private final LedgerRepository ledgerRepository;

    public TrialBalanceService(final LedgerRepository ledgerRepository) {
        super();
        this.ledgerRepository = ledgerRepository;
    }

    public TrialBalance getTrialBalance(final boolean includeEmptyEntries) {
        final TrialBalance trialBalance = new TrialBalance();
        this.ledgerRepository.findByParentLedgerIsNull().forEach(ledgerEntity
                -> this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity).forEach(subLedger -> {
                    final BigDecimal totalValue = subLedger.getTotalValue() != null ? subLedger.getTotalValue() : BigDecimal.ZERO;
                    if (!includeEmptyEntries && totalValue.compareTo(BigDecimal.ZERO) == 0) {
                        return;
                    }
                    final TrialBalanceEntry trialBalanceEntry = new TrialBalanceEntry();
                    trialBalanceEntry.setLedger(LedgerData.map(subLedger));
                    switch (subLedger.getType()) {
                        case ASSET:
                        case EXPENSE:
                            trialBalanceEntry.setType(TrialBalanceEntry.Type.DEBIT.name());
                            break;
                        case LIABILITY:
                        case EQUITY:
                        case REVENUE:
                            trialBalanceEntry.setType(TrialBalanceEntry.Type.CREDIT.name());
                            break;
                    }
                    trialBalanceEntry.setAmount(totalValue);
                    trialBalance.getTrialBalanceEntries().add(trialBalanceEntry);
                })
        );

        trialBalance.setDebitTotal(
                trialBalance.getTrialBalanceEntries()
                        .stream()
                        .filter(trialBalanceEntry -> trialBalanceEntry.getType().equals(TrialBalanceEntry.Type.DEBIT.name()))
                        .map(TrialBalanceEntry::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        trialBalance.setCreditTotal(
                trialBalance.getTrialBalanceEntries()
                        .stream()
                        .filter(trialBalanceEntry -> trialBalanceEntry.getType().equals(TrialBalanceEntry.Type.CREDIT.name()))
                        .map(TrialBalanceEntry::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        // Sort by ledger identifier ASC
        trialBalance.getTrialBalanceEntries()
                .sort(Comparator.comparing(trailBalanceEntry -> trailBalanceEntry.getLedger().getIdentifier()));

        return trialBalance;
    }
}

package io.smarthealth.accounting.acc.service;

import io.smarthealth.accounting.acc.data.mapper.LedgerMapper;
import io.smarthealth.accounting.acc.data.v1.AccountType;
import io.smarthealth.accounting.acc.data.v1.financial.statement.TrialBalance;
import io.smarthealth.accounting.acc.data.v1.financial.statement.TrialBalanceEntry;
import io.smarthealth.accounting.acc.domain.LedgerRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrialBalancesServices {

    private final LedgerRepository ledgerRepository;

    @Autowired
    public TrialBalancesServices(final LedgerRepository ledgerRepository) {
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
                    trialBalanceEntry.setLedger(LedgerMapper.map(subLedger));
                    switch (AccountType.valueOf(subLedger.getType())) {
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
        trialBalance.getTrialBalanceEntries().sort(Comparator.comparing(trailBalanceEntry -> trailBalanceEntry.getLedger().getIdentifier()));

        return trialBalance;
    }
}

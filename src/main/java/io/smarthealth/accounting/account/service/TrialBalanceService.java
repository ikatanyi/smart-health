package io.smarthealth.accounting.account.service;

import io.smarthealth.accounting.account.data.AccountData;
import io.smarthealth.accounting.account.data.TrialBalance;
import io.smarthealth.accounting.account.domain.AccountRepository; 
import io.smarthealth.accounting.account.domain.TrialBalanceEntry;
import java.math.BigDecimal;
import java.util.Comparator;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class TrialBalanceService {

    private final AccountRepository ledgerRepository;

    public TrialBalanceService(final AccountRepository ledgerRepository) {
        super();
        this.ledgerRepository = ledgerRepository;
    }

    public TrialBalance getTrialBalance(final boolean includeEmptyEntries) {
         TrialBalance trialBalance = new TrialBalance();
        this.ledgerRepository.findByParentAccountIsNull().forEach(ledgerEntity
                -> this.ledgerRepository.findByParentAccountOrderByAccountNumber(ledgerEntity).forEach(account -> {
                    final BigDecimal totalValue = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
                    if (!includeEmptyEntries && totalValue.compareTo(BigDecimal.ZERO) == 0) {
                        return;
                    }
                    final TrialBalanceEntry trialBalanceEntry = new TrialBalanceEntry();
                    trialBalanceEntry.setAccountData(AccountData.map(account));
                     switch (account.getAccountType().getGlAccountType()) {
                        case ASSET:
                        case EXPENSE:
                            trialBalanceEntry.setType(TrialBalanceEntry.Type.DEBIT);
                            break;
                        case LIABILITY:
                        case EQUITY:
                        case REVENUE:
                            trialBalanceEntry.setType(TrialBalanceEntry.Type.CREDIT);
                            break;
                    }
                    trialBalanceEntry.setAmount(totalValue);
                    trialBalance.getTrialBalanceEntries().add(trialBalanceEntry);
                })
        );

        trialBalance.setDebitTotal(
                trialBalance.getTrialBalanceEntries().isEmpty() ? BigDecimal.ZERO : trialBalance.getTrialBalanceEntries()
                        .stream()
                        .filter(trialBalanceEntry -> trialBalanceEntry.getType().equals(TrialBalanceEntry.Type.DEBIT.name()))
                        .map(TrialBalanceEntry::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        trialBalance.setCreditTotal(
                 trialBalance.getTrialBalanceEntries().isEmpty() ? BigDecimal.ZERO : trialBalance.getTrialBalanceEntries()
                        .stream()
                        .filter(trialBalanceEntry -> trialBalanceEntry.getType().equals(TrialBalanceEntry.Type.CREDIT.name()))
                        .map(TrialBalanceEntry::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        // Sort by ledger identifier ASC
        trialBalance.getTrialBalanceEntries().sort(Comparator.comparing(trailBalanceEntry -> trailBalanceEntry.getAccountData().getAccountNumber()));

        return trialBalance;
    }
 
}

package io.smarthealth.accounting.accounts.service;

import io.smarthealth.accounting.accounts.data.LedgerData;
import io.smarthealth.accounting.accounts.data.financial.statement.TrialBalance;
import io.smarthealth.accounting.accounts.data.financial.statement.TrialBalanceEntry;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.AccountRepository;
import io.smarthealth.accounting.accounts.domain.JournalEntryItemRepository;
import io.smarthealth.accounting.accounts.domain.Ledger;
import io.smarthealth.accounting.accounts.domain.LedgerRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TrialBalanceService {

    private final LedgerRepository ledgerRepository;
    private final JournalEntryItemRepository journalEntryItemRepository;
    private final AccountRepository accountRepository;

    public TrialBalanceService(LedgerRepository ledgerRepository, JournalEntryItemRepository journalEntryItemRepository, AccountRepository accountRepository) {
        super();
        this.ledgerRepository = ledgerRepository;
        this.journalEntryItemRepository = journalEntryItemRepository;
        this.accountRepository = accountRepository;
    }

    public TrialBalance getTrialBalance(final boolean includeEmptyEntries, LocalDate date) {
        final TrialBalance trialBalance = new TrialBalance();
        trialBalance.setAsAt(date != null ? date : LocalDate.now());

        this.ledgerRepository.findByParentLedgerIsNull()
                .forEach(ledgerEntity
                        -> this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity)
                        .forEach(subLedger -> {
                            BigDecimal totalValue = subLedger.getTotalValue() != null ? subLedger.getTotalValue() : BigDecimal.ZERO;
                            if (date != null) {
                                totalValue = calculateTotalAsAt(subLedger, date);
                            }

                            if (!includeEmptyEntries && totalValue.compareTo(BigDecimal.ZERO) == 0) {
                                return;
                            }
                            final TrialBalanceEntry trialBalanceEntry = new TrialBalanceEntry();
                            subLedger.setTotalValue(totalValue);
                            trialBalanceEntry.setLedger(LedgerData.map(subLedger));
                            switch (subLedger.getAccountType()) {
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

package io.smarthealth.accounting.accounts.service;

import io.smarthealth.accounting.accounts.data.ChartOfAccountEntry;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.AccountRepository;
import io.smarthealth.accounting.accounts.domain.Ledger;
import io.smarthealth.accounting.accounts.domain.LedgerRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChartOfAccountServices {

    private final LedgerRepository ledgerRepository;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public List<ChartOfAccountEntry> getChartOfAccounts() {
        final ArrayList<ChartOfAccountEntry> chartOfAccountEntries = new ArrayList<>();

        final List<Ledger> parentLedgers = this.ledgerRepository.findByParentLedgerIsNull();
        parentLedgers.sort(Comparator.comparing(Ledger::getIdentifier));

        final int level = 0;
        parentLedgers.forEach(ledgerEntity -> {
            final ChartOfAccountEntry chartOfAccountEntry = new ChartOfAccountEntry();
            chartOfAccountEntries.add(chartOfAccountEntry);
            chartOfAccountEntry.setCode(ledgerEntity.getIdentifier());
            chartOfAccountEntry.setName(ledgerEntity.getName());
            chartOfAccountEntry.setDescription(ledgerEntity.getDescription());
            chartOfAccountEntry.setType(ledgerEntity.getType());
            chartOfAccountEntry.setLevel(level);
            final int nextLevel = level + 1;
            this.traverseHierarchy(chartOfAccountEntries, nextLevel, ledgerEntity);
        });

        return chartOfAccountEntries;
    }

    private void traverseHierarchy(final List<ChartOfAccountEntry> chartOfAccountEntries, final int level, final Ledger ledgerEntity) {
        if (ledgerEntity.getShowAccountsInChart()) {
            final List<Account> accountEntities = this.accountRepository.findByLedger(ledgerEntity);
            accountEntities.sort(Comparator.comparing(Account::getIdentifier));
            accountEntities.forEach(accountEntity -> {
                final ChartOfAccountEntry chartOfAccountEntry = new ChartOfAccountEntry();
                chartOfAccountEntries.add(chartOfAccountEntry);
                chartOfAccountEntry.setCode(accountEntity.getIdentifier());
                chartOfAccountEntry.setName(accountEntity.getName());
                chartOfAccountEntry.setType(accountEntity.getType());
                chartOfAccountEntry.setLevel(level);
            });
        }

        final List<Ledger> subLedgers = this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity);
        if (subLedgers != null && subLedgers.size() > 0) {
            subLedgers.sort(Comparator.comparing(Ledger::getIdentifier));
            subLedgers.forEach(subLedger -> {
                final ChartOfAccountEntry chartOfAccountEntry = new ChartOfAccountEntry();
                chartOfAccountEntries.add(chartOfAccountEntry);
                chartOfAccountEntry.setCode(subLedger.getIdentifier());
                chartOfAccountEntry.setName(subLedger.getName());
                chartOfAccountEntry.setType(subLedger.getType());
                chartOfAccountEntry.setLevel(level);
                final int nextLevel = level + 1;
                this.traverseHierarchy(chartOfAccountEntries, nextLevel, subLedger);
            });
        }
    }
}

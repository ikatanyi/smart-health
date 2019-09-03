/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.service;

import io.smarthealth.financial.account.data.ChartOfAccountEntry;
import io.smarthealth.financial.account.domain.Account;
import io.smarthealth.financial.account.domain.AccountRepository;
import io.smarthealth.financial.account.domain.Ledger;
import io.smarthealth.financial.account.domain.LedgerRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
public class ChartOfAccountsService {

    private final LedgerRepository ledgerRepository;
    private final AccountRepository accountRepository;

    public ChartOfAccountsService(final LedgerRepository ledgerRepository, final AccountRepository accountRepository) {
        super();
        this.ledgerRepository = ledgerRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<ChartOfAccountEntry> getChartOfAccounts() {
        final ArrayList<ChartOfAccountEntry> chartOfAccountEntries = new ArrayList<>();

        final List<Ledger> parentLedgers = this.ledgerRepository.findByParentLedgerIsNull();
        parentLedgers.sort(Comparator.comparing(Ledger::getIdentifier));

        final int level = 0;
        parentLedgers.forEach(ledgerEntity -> {
            final ChartOfAccountEntry coa = new ChartOfAccountEntry();
            chartOfAccountEntries.add(coa);
            coa.setCode(ledgerEntity.getIdentifier());
            coa.setName(ledgerEntity.getName());
            coa.setDescription(ledgerEntity.getDescription());
            coa.setType(ledgerEntity.getType());
            coa.setLevel(level);
            final int nextLevel = level + 1;
            this.traverseHierarchy(chartOfAccountEntries, nextLevel, ledgerEntity);
        });

        return chartOfAccountEntries;
    }

    private void traverseHierarchy(final List<ChartOfAccountEntry> chartOfAccountEntries, final int level, final Ledger ledgerEntity) {
        if (ledgerEntity.getShowAccountsInChart()) {
            final List<Account> accountEntities = this.accountRepository.findByLedger(ledgerEntity);
            accountEntities.sort(Comparator.comparing(Account::getIdentifier));
            accountEntities.forEach(acc -> {
                final ChartOfAccountEntry chartOfAccountEntry = new ChartOfAccountEntry();
                chartOfAccountEntries.add(chartOfAccountEntry);
                chartOfAccountEntry.setCode(acc.getIdentifier());
                chartOfAccountEntry.setName(acc.getName());
                chartOfAccountEntry.setType(acc.getType());
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

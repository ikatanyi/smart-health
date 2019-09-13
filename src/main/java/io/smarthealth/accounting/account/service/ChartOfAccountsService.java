/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.service;

import io.smarthealth.accounting.account.data.ChartOfAccountEntry;
import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.accounting.account.domain.AccountRepository; 
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
 
    private final AccountRepository accountRepository;

    public ChartOfAccountsService(final AccountRepository accountRepository) {
        super(); 
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<ChartOfAccountEntry> getChartOfAccounts() {
        final ArrayList<ChartOfAccountEntry> chartOfAccountEntries = new ArrayList<>();

        final List<Account> parentAccount = this.accountRepository.findByParentAccountIsNull();
        parentAccount.sort(Comparator.comparing(Account::getAccountNumber));

        final int level = 0;
        parentAccount.forEach(ledgerEntity -> {
            final ChartOfAccountEntry coa = new ChartOfAccountEntry();
            chartOfAccountEntries.add(coa);
            coa.setCode(ledgerEntity.getAccountNumber());
            coa.setName(ledgerEntity.getAccountName());
            coa.setDescription(ledgerEntity.getDescription());
            coa.setType(ledgerEntity.getAccountType().getType());
            coa.setLevel(level);
            final int nextLevel = level + 1;
            this.traverseHierarchy(chartOfAccountEntries, nextLevel, ledgerEntity);
        });

        return chartOfAccountEntries;
    }

    private void traverseHierarchy(final List<ChartOfAccountEntry> chartOfAccountEntries, final int level, final Account parentAccount) {
//        if (parentAccount.getShowAccountsInChart()) {
//            final List<Account> accountEntities = this.accountRepository.findByParentAccountOrderByAccountNumber(parentAccount);
//            accountEntities.sort(Comparator.comparing(Account::getAccountNumber));
//            accountEntities.forEach(acc -> {
//                final ChartOfAccountEntry chartOfAccountEntry = new ChartOfAccountEntry();
//                chartOfAccountEntries.add(chartOfAccountEntry);
//                chartOfAccountEntry.setCode(acc.getAccountNumber());
//                chartOfAccountEntry.setName(acc.getAccountName());
//                chartOfAccountEntry.setType(acc.getAccountType().name());
//                chartOfAccountEntry.setLevel(level);
//            });
//        }
        final List<Account> subLedgers = this.accountRepository.findByParentAccountOrderByAccountNumber(parentAccount);
        if (subLedgers != null && subLedgers.size() > 0) {
            subLedgers.sort(Comparator.comparing(Account::getAccountNumber));
            subLedgers.forEach(subLedger -> {
                final ChartOfAccountEntry chartOfAccountEntry = new ChartOfAccountEntry();
                chartOfAccountEntries.add(chartOfAccountEntry);
                chartOfAccountEntry.setCode(subLedger.getAccountNumber());
                chartOfAccountEntry.setName(subLedger.getAccountName());
                chartOfAccountEntry.setType(subLedger.getAccountType().getType());
                chartOfAccountEntry.setLevel(level);
                final int nextLevel = level + 1;
                this.traverseHierarchy(chartOfAccountEntries, nextLevel, subLedger);
            });
        }
    }
}

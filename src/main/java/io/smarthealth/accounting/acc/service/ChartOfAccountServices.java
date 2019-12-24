package io.smarthealth.accounting.acc.service;

import io.smarthealth.accounting.acc.data.v1.ChartOfAccountEntry;
import io.smarthealth.accounting.acc.domain.AccountEntity;
import io.smarthealth.accounting.acc.domain.AccountRepository;
import io.smarthealth.accounting.acc.domain.LedgerEntity;
import io.smarthealth.accounting.acc.domain.LedgerRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChartOfAccountServices {

  private final LedgerRepository ledgerRepository;
  private final AccountRepository accountRepository;

  @Autowired
  public ChartOfAccountServices(final LedgerRepository ledgerRepository, final AccountRepository accountRepository) {
    super();
    this.ledgerRepository = ledgerRepository;
    this.accountRepository = accountRepository;
  }

  @Transactional(readOnly = true)
  public List<ChartOfAccountEntry> getChartOfAccounts() {
    final ArrayList<ChartOfAccountEntry> chartOfAccountEntries = new ArrayList<>();

    final List<LedgerEntity> parentLedgers = this.ledgerRepository.findByParentLedgerIsNull();
    parentLedgers.sort(Comparator.comparing(LedgerEntity::getIdentifier));

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

  private void traverseHierarchy(final List<ChartOfAccountEntry> chartOfAccountEntries, final int level, final LedgerEntity ledgerEntity) {
    if (ledgerEntity.getShowAccountsInChart()) {
      final List<AccountEntity> accountEntities = this.accountRepository.findByLedger(ledgerEntity);
      accountEntities.sort(Comparator.comparing(AccountEntity::getIdentifier));
      accountEntities.forEach(accountEntity -> {
        final ChartOfAccountEntry chartOfAccountEntry = new ChartOfAccountEntry();
        chartOfAccountEntries.add(chartOfAccountEntry);
        chartOfAccountEntry.setCode(accountEntity.getIdentifier());
        chartOfAccountEntry.setName(accountEntity.getName());
        chartOfAccountEntry.setType(accountEntity.getType());
        chartOfAccountEntry.setLevel(level);
      });
    }

    final List<LedgerEntity> subLedgers = this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity);
    if (subLedgers != null && subLedgers.size() > 0) {
      subLedgers.sort(Comparator.comparing(LedgerEntity::getIdentifier));
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

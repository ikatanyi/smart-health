package io.smarthealth.accounting.acc.service;

import io.smarthealth.accounting.acc.data.mapper.AccountMapper;
import io.smarthealth.accounting.acc.data.mapper.LedgerMapper;
import io.smarthealth.accounting.acc.data.v1.Account;
import io.smarthealth.accounting.acc.data.v1.AccountPage;
import io.smarthealth.accounting.acc.data.v1.Ledger;
import io.smarthealth.accounting.acc.data.v1.LedgerPage;
import io.smarthealth.accounting.acc.domain.AccountEntity;
import io.smarthealth.accounting.acc.domain.LedgerEntity;
import io.smarthealth.accounting.acc.domain.LedgerRepository;
import io.smarthealth.accounting.acc.domain.specification.LedgerSpecification;
import io.smarthealth.accounting.acc.validation.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.accounting.acc.domain.AccountRepository;

@Service
@Slf4j
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public LedgerService(final LedgerRepository ledgerRepository,
            final AccountRepository accountRepository) {
        super();
        this.ledgerRepository = ledgerRepository;
        this.accountRepository = accountRepository;
    }

    public LedgerPage fetchLedgers(final boolean includeSubLedgers,
            final String term,
            final String type,
            final Pageable pageable) {
        final LedgerPage ledgerPage = new LedgerPage();

        final Page<LedgerEntity> ledgerEntities = this.ledgerRepository.findAll(
                LedgerSpecification.createSpecification(includeSubLedgers, term, type), pageable
        );

        ledgerPage.setTotalPages(ledgerEntities.getTotalPages());
        ledgerPage.setTotalElements(ledgerEntities.getTotalElements());

        ledgerPage.setLedgers(this.mapToLedger(ledgerEntities.getContent()));

        return ledgerPage;
    }

    private List<Ledger> mapToLedger(List<LedgerEntity> ledgerEntities) {
        final List<Ledger> result = new ArrayList<>(ledgerEntities.size());

        if (!ledgerEntities.isEmpty()) {
            ledgerEntities.forEach(ledgerEntity -> {
                final Ledger ledger = LedgerMapper.map(ledgerEntity);
                this.addSubLedgers(ledger, this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity));
                result.add(ledger);
            });
        }

        return result;
    }

    public Optional<Ledger> findLedger(final String identifier) {
        final LedgerEntity ledgerEntity = this.ledgerRepository.findByIdentifier(identifier);
        if (ledgerEntity != null) {
            final Ledger ledger = LedgerMapper.map(ledgerEntity);
            this.addSubLedgers(ledger, this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity));
            return Optional.of(ledger);
        } else {
            return Optional.empty();
        }
    }

    public AccountPage fetchAccounts(final String ledgerIdentifier, final Pageable pageable) {
        final LedgerEntity ledgerEntity = this.ledgerRepository.findByIdentifier(ledgerIdentifier);
        final Page<AccountEntity> accountEntities = this.accountRepository.findByLedger(ledgerEntity, pageable);

        final AccountPage accountPage = new AccountPage();
        accountPage.setTotalPages(accountEntities.getTotalPages());
        accountPage.setTotalElements(accountEntities.getTotalElements());

        if (accountEntities.getSize() > 0) {
            final List<Account> accounts = new ArrayList<>(accountEntities.getSize());
            accountEntities.forEach(accountEntity -> accounts.add(AccountMapper.map(accountEntity)));
            accountPage.setAccounts(accounts);
        }

        return accountPage;
    }

    public boolean hasAccounts(final String ledgerIdentifier) {
        final LedgerEntity ledgerEntity = this.ledgerRepository.findByIdentifier(ledgerIdentifier);
        final List<AccountEntity> ledgerAccounts = this.accountRepository.findByLedger(ledgerEntity);
        return ledgerAccounts.size() > 0;
    }

    private void addSubLedgers(final Ledger parentLedger,
            final List<LedgerEntity> subLedgerEntities) {
        if (subLedgerEntities != null) {
            final List<Ledger> subLedgers = new ArrayList<>(subLedgerEntities.size());
            subLedgerEntities.forEach(subLedgerEntity -> subLedgers.add(LedgerMapper.map(subLedgerEntity)));
            parentLedger.setSubLedgers(subLedgers);
        }
    }
    
    
  @Transactional
  public String createLedger(final Ledger createLedgerCommand) {
    final Ledger ledger = createLedgerCommand;

    log.debug("Received create ledger command with identifier {}.", ledger.getIdentifier());

    final LedgerEntity parentLedgerEntity = new LedgerEntity();
    parentLedgerEntity.setIdentifier(ledger.getIdentifier());
    parentLedgerEntity.setType(ledger.getType());
    parentLedgerEntity.setName(ledger.getName());
    parentLedgerEntity.setDescription(ledger.getDescription());
    parentLedgerEntity.setShowAccountsInChart(ledger.getShowAccountsInChart());
    final LedgerEntity savedParentLedger = this.ledgerRepository.save(parentLedgerEntity);
    this.addSubLedgersInternal(ledger.getSubLedgers(), savedParentLedger);

    log.debug("Ledger {} created.", ledger.getIdentifier());

    return ledger.getIdentifier();
  }

  @Transactional
  public String addSubLedger(String parentLedgerIdentifier, Ledger subLedger) {
    final LedgerEntity parentLedger =
        this.ledgerRepository.findByIdentifier(parentLedgerIdentifier); 
    final LedgerEntity subLedgerEntity = this.ledgerRepository.findByIdentifier(subLedger.getIdentifier());
    if (subLedgerEntity == null) {
      this.addSubLedgersInternal(Collections.singletonList(subLedger), parentLedger);
    } else {
      subLedgerEntity.setParentLedger(parentLedger);
      this.ledgerRepository.save(subLedgerEntity);
    }
    this.ledgerRepository.save(parentLedger);
    return subLedger.getIdentifier();
  }

  @Transactional
  public String modifyLedger(final Ledger modifyLedgerCommand) {
    final Ledger ledger2modify = modifyLedgerCommand;
    final LedgerEntity ledgerEntity =
        this.ledgerRepository.findByIdentifier(ledger2modify.getIdentifier());
    ledgerEntity.setName(ledger2modify.getName());
    ledgerEntity.setDescription(ledger2modify.getDescription());
    ledgerEntity.setShowAccountsInChart(ledger2modify.getShowAccountsInChart());
    this.ledgerRepository.save(ledgerEntity);
    return ledger2modify.getIdentifier();
  }

  @Transactional
  public String deleteLedger(String identifier) {
    this.ledgerRepository.delete(this.ledgerRepository.findByIdentifier(identifier));
    return identifier;
  }

  @Transactional
  public void addSubLedgersInternal(final List<Ledger> subLedgers, final LedgerEntity parentLedgerEntity) {
    if (subLedgers != null) {

      log.debug(
          "Add {} sub ledger(s) to parent ledger {}.", subLedgers.size(),
          parentLedgerEntity.getIdentifier()
      );

      for (final Ledger subLedger : subLedgers) {
        if (!subLedger.getType().equals(parentLedgerEntity.getType())) {
          log.error(
              "Type of sub ledger {} must match parent ledger {}. Expected {}, was {}",
              subLedger.getIdentifier(), parentLedgerEntity.getIdentifier(),
              parentLedgerEntity.getType(), subLedger.getType()
          );

          throw ServiceException.badRequest(
              "Type of sub ledger {0} must match parent ledger {1}. Expected {2}, was {3}",
              subLedger.getIdentifier(), parentLedgerEntity.getIdentifier(),
              parentLedgerEntity.getType(), subLedger.getType()
          );
        }
        final LedgerEntity subLedgerEntity = new LedgerEntity();
        subLedgerEntity.setIdentifier(subLedger.getIdentifier());
        subLedgerEntity.setType(subLedger.getType());
        subLedgerEntity.setName(subLedger.getName());
        subLedgerEntity.setDescription(subLedger.getDescription()); 
        subLedgerEntity.setShowAccountsInChart(subLedger.getShowAccountsInChart());
        subLedgerEntity.setParentLedger(parentLedgerEntity);
        final LedgerEntity savedSubLedger = this.ledgerRepository.save(subLedgerEntity);
        this.addSubLedgersInternal(subLedger.getSubLedgers(), savedSubLedger); 
      }
    }
  }
}

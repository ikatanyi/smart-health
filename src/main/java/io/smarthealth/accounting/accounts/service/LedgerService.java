package io.smarthealth.accounting.accounts.service;

import io.smarthealth.accounting.accounts.data.AccountData;
import io.smarthealth.accounting.accounts.data.AccountPage;
import io.smarthealth.accounting.accounts.data.LedgerData;
import io.smarthealth.accounting.accounts.data.LedgerGrouping;
import io.smarthealth.accounting.accounts.data.LedgerPage;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.AccountRepository;
import io.smarthealth.accounting.accounts.domain.AccountType;
import io.smarthealth.accounting.accounts.domain.Ledger;
import io.smarthealth.accounting.accounts.domain.LedgerRepository;
import io.smarthealth.accounting.accounts.domain.specification.LedgerSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.security.domain.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final AccountRepository accountRepository;

    public LedgerPage fetchLedgers(final Boolean includeSubLedgers,
            final String term,
            final String type,
            final Pageable pageable) {
        final LedgerPage ledgerPage = new LedgerPage();

        final Page<Ledger> ledgerEntities = this.ledgerRepository.findAll(
                LedgerSpecification.createSpecification(includeSubLedgers, term, type), pageable
        );

        ledgerPage.setTotalPages(ledgerEntities.getTotalPages());
        ledgerPage.setTotalElements(ledgerEntities.getTotalElements());

        ledgerPage.setLedgers(this.mapToLedger(ledgerEntities.getContent()));

        return ledgerPage;
    }

    public LedgerPage listLedgers(Pageable pageable) {
        final LedgerPage ledgerPage = new LedgerPage();

        Page<LedgerData> ledgerEntities = this.ledgerRepository.findAll(pageable)
                .map(LedgerData::map);

        ledgerPage.setTotalPages(ledgerEntities.getTotalPages());
        ledgerPage.setTotalElements(ledgerEntities.getTotalElements());

        ledgerPage.setLedgers(ledgerEntities.getContent());

        return ledgerPage;
    }

    public List<Ledger> listAllAccountTypes() {
        return ledgerRepository.findByParentLedgerNotNull();
    }
    
     public List<Ledger> listByAccountType(AccountType accountType) {
        return ledgerRepository.findByAccountType(accountType);
    }

    public ArrayList<LedgerGrouping> getGroupedAccountsTypes() {
        ArrayList<LedgerGrouping> list = new ArrayList<>();
        Map<AccountType, List<LedgerData>> accountTypes = listAllAccountTypes().stream()
                .map(LedgerData::map)
                .collect(groupingBy(LedgerData::getType));

        accountTypes.forEach((k, v) -> {
            list.add(new LedgerGrouping(k.name(), v));
        });

        return list;
    }

    private List<LedgerData> mapToLedger(List<Ledger> ledgerEntities) {
        final List<LedgerData> result = new ArrayList<>(ledgerEntities.size());

        if (!ledgerEntities.isEmpty()) {
            ledgerEntities.forEach(ledgerEntity -> {
                final LedgerData ledger = LedgerData.map(ledgerEntity);
                this.addSubLedgers(ledger, this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity));
                result.add(ledger);
            });
        }

        return result;
    }

    public Optional<Ledger> findLedger(String identifier) {
        return ledgerRepository.findByIdentifier(identifier);
    }

    public Ledger findLedgerOrThrow(String identifier) {
        return ledgerRepository.findByIdentifier(identifier)
                .orElseThrow(() -> APIException.notFound("Ledger with id  {0} not found"));
    }

    public Optional<LedgerData> findLedgerData(final String identifier) {
        final Optional<Ledger> ledgerEntity = findLedger(identifier);
        if (ledgerEntity.isPresent()) {
            final LedgerData ledger = LedgerData.map(ledgerEntity.get());
            this.addSubLedgers(ledger, this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity.get()));
            return Optional.of(ledger);
        } else {
            return Optional.empty();
        }
    }

    public AccountPage fetchAccounts(final String ledgerIdentifier, final Pageable pageable) {
        final Ledger ledgerEntity = findLedgerOrThrow(ledgerIdentifier);
        final Page<Account> accountEntities = this.accountRepository.findByLedger(ledgerEntity, pageable);

        final AccountPage accountPage = new AccountPage();
        accountPage.setTotalPages(accountEntities.getTotalPages());
        accountPage.setTotalElements(accountEntities.getTotalElements());

        if (accountEntities.getSize() > 0) {
            final List<AccountData> accounts = new ArrayList<>(accountEntities.getSize());
            accountEntities.forEach(accountEntity -> accounts.add(AccountData.map(accountEntity)));
            accountPage.setAccounts(accounts);
        }

        return accountPage;
    }

    public boolean hasAccounts(final String ledgerIdentifier) {
        final Ledger ledgerEntity = findLedgerOrThrow(ledgerIdentifier);
        final List<Account> ledgerAccounts = this.accountRepository.findByLedger(ledgerEntity);
        return ledgerAccounts.size() > 0;
    }

    private void addSubLedgers(final LedgerData parentLedger,
            final List<Ledger> subLedgerEntities) {
        if (subLedgerEntities != null) {
            final List<LedgerData> subLedgers = new ArrayList<>(subLedgerEntities.size());
            subLedgerEntities.forEach(subLedgerEntity -> subLedgers.add(LedgerData.map(subLedgerEntity)));
            parentLedger.setSubLedgers(subLedgers);
        }
    }

    @Transactional
    public String createLedger(final LedgerData ledgerData) {

        final Ledger parentLedgerEntity = new Ledger();
        parentLedgerEntity.setIdentifier(ledgerData.getIdentifier());
        parentLedgerEntity.setAccountType(ledgerData.getType());
        parentLedgerEntity.setName(ledgerData.getName());
        parentLedgerEntity.setDescription(ledgerData.getDescription());
        parentLedgerEntity.setShowAccountsInChart(ledgerData.getShowAccountsInChart());
        
        if(ledgerData.getParentLedgerIdentifier()!=null){
            Ledger parentLedger = findLedgerOrThrow(ledgerData.getParentLedgerIdentifier());
            parentLedgerEntity.setParentLedger(parentLedger);
        }

        final Ledger savedParentLedger = this.ledgerRepository.save(parentLedgerEntity);

        this.addSubLedgersInternal(ledgerData.getSubLedgers(), savedParentLedger);

        log.debug("Ledger {} created.", ledgerData.getIdentifier());

        return ledgerData.getIdentifier();
    }

    @Transactional
    public String addSubLedger(String parentLedgerIdentifier, LedgerData subLedger) {
        final Ledger parentLedger = findLedgerOrThrow(parentLedgerIdentifier);
        final Optional<Ledger> subLedgerEntity = this.ledgerRepository.findByIdentifier(subLedger.getIdentifier());
        if (!subLedgerEntity.isPresent()) {
            this.addSubLedgersInternal(Collections.singletonList(subLedger), parentLedger);
        } else {
            Ledger ledgerEntity = subLedgerEntity.get();
            ledgerEntity.setParentLedger(parentLedger);
            this.ledgerRepository.save(ledgerEntity);
        }
        this.ledgerRepository.save(parentLedger);
        return subLedger.getIdentifier();
    }

    @Transactional
    public String modifyLedger(final LedgerData ledgerData) {
        final Ledger ledgerEntity = findLedgerOrThrow(ledgerData.getIdentifier());
        ledgerEntity.setName(ledgerData.getName());
        ledgerEntity.setDescription(ledgerData.getDescription());
        ledgerEntity.setShowAccountsInChart(ledgerData.getShowAccountsInChart());
        this.ledgerRepository.save(ledgerEntity);
        return ledgerData.getIdentifier();
    }

    @Transactional
    public String deleteLedger(String identifier) {
        final Ledger ledgerEntity = findLedgerOrThrow(identifier);
        this.ledgerRepository.delete(ledgerEntity);
        return identifier;
    }

    @Transactional
    public void addSubLedgersInternal(final List<LedgerData> subLedgers, final Ledger parentLedgerEntity) {
        if (subLedgers != null) {

            log.debug("Add {} sub ledger(s) to parent ledger {}.", subLedgers.size(), parentLedgerEntity.getIdentifier());

            for (final LedgerData subLedger : subLedgers) {
                if (!subLedger.getType().equals(parentLedgerEntity.getAccountType())) {
                    log.error(
                            "Type of sub ledger {} must match parent ledger {}. Expected {}, was {}",
                            subLedger.getIdentifier(), parentLedgerEntity.getIdentifier(),
                            parentLedgerEntity.getAccountType(), subLedger.getType()
                    );

                    throw APIException.badRequest(
                            "Type of sub ledger {0} must match parent ledger {1}. Expected {2}, was {3}",
                            subLedger.getIdentifier(), parentLedgerEntity.getIdentifier(),
                            parentLedgerEntity.getAccountType(), subLedger.getType()
                    );
                }
                final Ledger subLedgerEntity = new Ledger();
                subLedgerEntity.setIdentifier(subLedger.getIdentifier());
                subLedgerEntity.setAccountType(subLedger.getType());
                subLedgerEntity.setName(subLedger.getName());
                subLedgerEntity.setDescription(subLedger.getDescription());
                subLedgerEntity.setShowAccountsInChart(subLedger.getShowAccountsInChart());
                subLedgerEntity.setParentLedger(parentLedgerEntity);
                final Ledger savedSubLedger = this.ledgerRepository.save(subLedgerEntity);
                this.addSubLedgersInternal(subLedger.getSubLedgers(), savedSubLedger);
            }
        }
    }
}

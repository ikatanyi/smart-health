package io.smarthealth.financial.account.service;

import io.smarthealth.financial.account.data.AccountData;
import io.smarthealth.financial.account.data.LedgerData;
import io.smarthealth.financial.account.domain.Account;
import io.smarthealth.financial.account.domain.AccountRepository;
import io.smarthealth.financial.account.domain.enumeration.AccountType;
import io.smarthealth.financial.account.domain.Ledger;
import io.smarthealth.financial.account.domain.LedgerRepository;
import io.smarthealth.financial.account.domain.specification.LedgerSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@Slf4j
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public LedgerService(LedgerRepository ledgerRepository, AccountRepository accountRepository, ModelMapper modelMapper) {
        this.ledgerRepository = ledgerRepository;
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
    }

    public Ledger createLedger(LedgerData ledgerData) {
        Ledger savedParentLedger = ledgerRepository.save(convertToEntity(ledgerData));
        addSubLedgersInternal(ledgerData.getSubLedgers(), savedParentLedger);
        log.debug("Ledger {} created.", ledgerData.getIdentifier());
        return savedParentLedger;
    }

    public Optional<LedgerData> findLedger(final String identifier) {
        final Optional<Ledger> ledgerEntity = this.ledgerRepository.findByIdentifier(identifier);
        if (ledgerEntity.isPresent()) {
            final LedgerData ledger = convertToData(ledgerEntity.get());
            this.addSubLedgers(ledger, this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity.get()));
            return Optional.of(ledger);
        } else {
            return Optional.empty();
        }
    }

    public Page<Ledger> fetchLedgers(final boolean includeSubLedgers, final String term, final String type, final Pageable pageable) {
        final Page<Ledger> ledgerEntities = this.ledgerRepository.findAll(LedgerSpecification.createSpecification(includeSubLedgers, term, type), pageable);
//                                                                            .map(ledger -> convertToData(ledger));
        return ledgerEntities;
    }

    public Page<Account> fetchAccounts(final String ledgerIdentifier, final Pageable pageable) {
        final Ledger ledgerEntity = this.ledgerRepository.findByIdentifier(ledgerIdentifier)
                .orElseThrow(() -> APIException.notFound("Ledger with Identifier {0} not found", ledgerIdentifier));
        final Page<Account> accountEntities = this.accountRepository.findByLedger(ledgerEntity, pageable);

        return accountEntities;
    }

    public String addSubLedger(LedgerData subLedger) {
        final Ledger parentLedger = ledgerRepository.findByIdentifier(subLedger.getParentLedgerIdentifier()).get();
        final Optional<Ledger> subLedgerEntity = this.ledgerRepository.findByIdentifier(subLedger.getIdentifier());
        if (!subLedgerEntity.isPresent()) {
            this.addSubLedgersInternal(Collections.singletonList(subLedger), parentLedger);
        } else {
            Ledger toSave = subLedgerEntity.get();
            toSave.setParentLedger(parentLedger);
            this.ledgerRepository.save(toSave);
        }

        this.ledgerRepository.save(parentLedger);
        return subLedger.getIdentifier();
    }

    public boolean hasAccounts(final String ledgerIdentifier) {
        final Ledger ledgerEntity = this.ledgerRepository.findByIdentifier(ledgerIdentifier)
                .orElseThrow(() -> APIException.notFound("Ledger with Identifier {0} not found", ledgerIdentifier));
        final List<Account> ledgerAccounts = this.accountRepository.findByLedger(ledgerEntity);
        return ledgerAccounts.size() > 0;
    }

    private void addSubLedgers(final LedgerData parentLedger, final List<Ledger> subLedgerEntities) {
        if (subLedgerEntities != null) {
            final List<LedgerData> subLedgers = new ArrayList<>(subLedgerEntities.size());
            subLedgerEntities.forEach(subLedgerEntity -> subLedgers.add(convertToData(subLedgerEntity)));
            parentLedger.setSubLedgers(subLedgers);
        }
    }

    public LedgerData convertToData(Ledger ledger) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        LedgerData data = modelMapper.map(ledger, LedgerData.class);
        return data;
    }

    public Ledger convertToEntity(LedgerData data) {
        Ledger ledger = modelMapper.map(data, Ledger.class);
        return ledger;
    }

    public AccountData convertToAccountData(Account account) {
        AccountData accdata = new AccountData();
        accdata.setType(AccountType.valueOf(account.getType()));
//        accdata.setBalance(account.getBalance());
        accdata.setIdentifier(account.getIdentifier());
        accdata.setLedger(account.getLedger() != null ? account.getLedger().getIdentifier() : null);
        accdata.setName(account.getName());
        accdata.setReferenceAccount(account.getReferenceAccount() != null ? account.getReferenceAccount().getIdentifier() : null);
      
        return accdata;
    }

    @Transactional
    public void addSubLedgersInternal(final List<LedgerData> subLedgers, final Ledger parentLedgerEntity) {
        if (subLedgers != null) {

            log.debug("Add {} sub ledger(s) to parent ledger {}.", subLedgers.size(), parentLedgerEntity.getIdentifier());

            for (final LedgerData subLedger : subLedgers) { 
                if (!subLedger.getType().name().equals(parentLedgerEntity.getType())) {
                    log.error("Type of sub ledger {} must match parent ledger {}. Expected {}, was {}", subLedger.getIdentifier(), parentLedgerEntity.getIdentifier(), parentLedgerEntity.getType(), subLedger.getType() );

                    throw APIException.badRequest( "Type of sub ledger {0} must match parent ledger {1}. Expected {2}, was {3}", subLedger.getIdentifier(), parentLedgerEntity.getIdentifier(), parentLedgerEntity.getType(), subLedger.getType() );
                }
                
                
                final Ledger subLedgerEntity = convertToEntity(subLedger);
                subLedgerEntity.setParentLedger(parentLedgerEntity);
                final Ledger savedSubLedger = this.ledgerRepository.save(subLedgerEntity);
                this.addSubLedgersInternal(subLedger.getSubLedgers(), savedSubLedger);

                log.debug("Sub ledger {} created.", subLedger.getIdentifier());
            }
        }
    }
}

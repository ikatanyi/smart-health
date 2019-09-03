/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.service;

import io.smarthealth.financial.account.data.LedgerData;
import io.smarthealth.financial.account.data.TrialBalance;
import io.smarthealth.financial.account.domain.enumeration.AccountType;
import io.smarthealth.financial.account.domain.Ledger;
import io.smarthealth.financial.account.domain.LedgerRepository;
import io.smarthealth.financial.account.domain.TrialBalanceEntry;
import java.math.BigDecimal;
import java.util.Comparator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class TrialBalanceService {

    private final LedgerRepository ledgerRepository;

    public TrialBalanceService(final LedgerRepository ledgerRepository) {
        super();
        this.ledgerRepository = ledgerRepository;
    }

    public TrialBalance getTrialBalance(final boolean includeEmptyEntries) {
         TrialBalance trialBalance = new TrialBalance();
        this.ledgerRepository.findByParentLedgerIsNull().forEach(ledgerEntity
                -> this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity).forEach(subLedger -> {
                    final BigDecimal totalValue = subLedger.getTotalValue() != null ? subLedger.getTotalValue() : BigDecimal.ZERO;
                    if (!includeEmptyEntries && totalValue.compareTo(BigDecimal.ZERO) == 0) {
                        return;
                    }
                    final TrialBalanceEntry trialBalanceEntry = new TrialBalanceEntry();
                    trialBalanceEntry.setLedger(convertToData(subLedger));
                     switch (AccountType.valueOf(subLedger.getType())) {
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
        trialBalance.getTrialBalanceEntries().sort(Comparator.comparing(trailBalanceEntry -> trailBalanceEntry.getLedger().getIdentifier()));

        return trialBalance;
    }

    public LedgerData convertToData(Ledger ledger) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        LedgerData data = modelMapper.map(ledger, LedgerData.class);
        return data;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.service;

import io.smarthealth.financial.account.data.TransactionTypeData;
import io.smarthealth.financial.account.domain.TransactionType;
import io.smarthealth.financial.account.domain.TransactionTypeRepository;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class TransactionTypeService {
    private final TransactionTypeRepository transactionTypeRepository;
    private final ModelMapper modelMapper;
 
  public TransactionTypeService(final TransactionTypeRepository transactionTypeRepository, ModelMapper modelMapper) {
    super();
    this.transactionTypeRepository = transactionTypeRepository;
    this.modelMapper=modelMapper;
  }

  public Page<TransactionTypeData> fetchTransactionTypes(final String term, final Pageable pageable) {
    final Page<TransactionType> transactionTypeEntityPage;
    if (term != null) {
      transactionTypeEntityPage =
          this.transactionTypeRepository.findByCodeContainingOrNameContaining(term, term, pageable);
    } else {
      transactionTypeEntityPage = this.transactionTypeRepository.findAll(pageable);
    }
  
     Page<TransactionTypeData> transactionTypePage=transactionTypeEntityPage.map(tx -> convertToData(tx));

    return transactionTypePage;
  }

  public Optional<TransactionTypeData> findByIdentifier(final String identifier) {
    return this.transactionTypeRepository
            .findByCode(identifier).map(tx -> convertToData(tx));
  }
  
      public TransactionTypeData convertToData(TransactionType ledger) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        TransactionTypeData data = modelMapper.map(ledger, TransactionTypeData.class);
        return data;
    }

    public TransactionType convertToEntity(TransactionTypeData data) {
        TransactionType ledger = modelMapper.map(data, TransactionType.class);
        return ledger;
    }
}

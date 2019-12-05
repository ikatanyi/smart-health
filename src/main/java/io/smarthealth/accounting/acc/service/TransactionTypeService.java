package io.smarthealth.accounting.acc.service;

 
import io.smarthealth.accounting.acc.data.mapper.TransactionTypeMapper;
import io.smarthealth.accounting.acc.data.v1.TransactionType;
import io.smarthealth.accounting.acc.data.v1.TransactionTypePage;
import io.smarthealth.accounting.acc.domain.TransactionTypeEntity;
import io.smarthealth.accounting.acc.domain.TransactionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TransactionTypeService {

  private final TransactionTypeRepository transactionTypeRepository;

  @Autowired
  public TransactionTypeService(final TransactionTypeRepository transactionTypeRepository) {
    super();
    this.transactionTypeRepository = transactionTypeRepository;
  }

  public TransactionTypePage fetchTransactionTypes(final String term, final Pageable pageable) {
    final Page<TransactionTypeEntity> transactionTypeEntityPage;
    if (term != null) {
      transactionTypeEntityPage =
          this.transactionTypeRepository.findByIdentifierContainingOrNameContaining(term, term, pageable);
    } else {
      transactionTypeEntityPage = this.transactionTypeRepository.findAll(pageable);
    }

    final TransactionTypePage transactionTypePage = new TransactionTypePage();
    transactionTypePage.setTotalElements(transactionTypeEntityPage.getTotalElements());
    transactionTypePage.setTotalPages(transactionTypeEntityPage.getTotalPages());

    transactionTypePage.setTransactionTypes(new ArrayList<>(transactionTypeEntityPage.getSize()));
    transactionTypeEntityPage.forEach(transactionTypeEntity ->
        transactionTypePage.add(TransactionTypeMapper.map(transactionTypeEntity)));

    return transactionTypePage;
  }

  public Optional<TransactionType> findByIdentifier(final String identifier) {
    return this.transactionTypeRepository.findByIdentifier(identifier).map(TransactionTypeMapper::map);
  }
  
  @Transactional 
  public String createTransactionType(TransactionType transactionType) { 

    this.transactionTypeRepository.save(TransactionTypeMapper.map(transactionType));

    return transactionType.getCode();
  }

  @Transactional
  public String changeTransactionType(TransactionType transactionType) { 

    final Optional<TransactionTypeEntity> optionalTransactionTypeEntity =
        this.transactionTypeRepository.findByIdentifier(transactionType.getCode());

    optionalTransactionTypeEntity.ifPresent(transactionTypeEntity -> {
      transactionTypeEntity.setName(transactionType.getName());
      transactionTypeEntity.setDescription(transactionType.getDescription());
      this.transactionTypeRepository.save(transactionTypeEntity);
    });

    return transactionType.getCode();
  }
}

package io.smarthealth.financial.account.domain;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByReferenceNoAndTransactionDateBetween(final String refNumber, final LocalDateTime dateFrom, final LocalDateTime dateTo,final Pageable pageable);
    
    Page<Transaction> findByReferenceNoAndTransactionDateBetweenAndDescriptionContaining(final String refNumber, final LocalDateTime dateFrom, final LocalDateTime dateTo, final String message, final Pageable pageable);
}

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

    Page<Transaction> findByAccountAndTransactionDateBetween(final Account account, final LocalDateTime dateFrom, final LocalDateTime dateTo,final Pageable pageable);
    Page<Transaction> findByAccountAndTransactionDateBetweenAndMessageEquals(final Account account, final LocalDateTime dateFrom, final LocalDateTime dateTo, final String message, final Pageable pageable);
}

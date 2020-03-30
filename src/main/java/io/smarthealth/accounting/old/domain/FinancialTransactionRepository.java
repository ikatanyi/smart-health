package io.smarthealth.accounting.old.domain;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */@Deprecated
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long>, JpaSpecificationExecutor<FinancialTransaction> {

    Page<FinancialTransaction> findByDateBetween(final String customer,
            final LocalDateTime dateFrom,
            final LocalDateTime dateTo,
            final Pageable pageable);
}

package io.smarthealth.accounting.account.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface JournalRepository extends JpaRepository<Journal, Long>,JpaSpecificationExecutor<Journal>{
    Optional<Journal> findByTransactionId(final String identifier);
    
    List<Journal> findByDocumentDateBetween(LocalDate from, LocalDate to);
}

package io.smarthealth.accounting.accounts.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface JournalRepository extends JpaRepository<JournalEntry, Long>, JpaSpecificationExecutor<JournalEntry> {

    Optional<JournalEntry> findByTransactionNo(String transactionNo);
}

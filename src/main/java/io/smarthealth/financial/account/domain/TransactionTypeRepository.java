package io.smarthealth.financial.account.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {

    Page<TransactionType> findByCodeContainingOrNameContaining(final String identifier, final String name, final Pageable pageable);

    Optional<TransactionType> findByCode(final String identifier);
}

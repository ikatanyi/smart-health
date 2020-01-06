package io.smarthealth.accounting.acc.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionTypeEntity, Long> {

    Page<TransactionTypeEntity> findByIdentifierContainingOrNameContaining(final String identifier,
            final String name,
            final Pageable pageable);

    Optional<TransactionTypeEntity> findByIdentifier(final String identifier);
}

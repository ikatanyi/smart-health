package io.smarthealth.accounting.accounts.domain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    List<Account> findByLedger(final Ledger ledgerEntity);

    Page<Account> findByLedger(final Ledger ledgerEntity, final Pageable pageable);

    Optional<Account> findByIdentifier(final String identifier);

    List<Account> findByType(AccountType type);

    @Query("SELECT CASE WHEN count(a) > 0 THEN true ELSE false END FROM Account a where a.referenceAccount = :accountEntity")
    Boolean existsByReference(@Param("accountEntity") final Account accountEntity);

    Stream<Account> findByBalanceIsNot(final Double value);
}

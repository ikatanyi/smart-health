package io.smarthealth.financial.account.domain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    Optional<Account> findByIdentifier(final String accountCode);

    @Query("SELECT CASE WHEN count(a) > 0 THEN true ELSE false END FROM Account a where a.identifier = :account")
    Boolean existsByAccount(@Param("account") final String accountCode);

    List<Account> findByLedger(final Ledger ledger);

    Page<Account> findByLedger(final Ledger ledger, final Pageable pageable);

    @Query("SELECT CASE WHEN count(a) > 0 THEN true ELSE false END FROM Account a where a.referenceAccount = :account")
    Boolean existsByReference(@Param("account") final Account account);

    Stream<Account> findByBalanceIsNot(final Double value);

}

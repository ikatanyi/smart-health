package io.smarthealth.financial.account.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    Optional<Account> findByAccountCode(final String accountCode);

    @Query("SELECT CASE WHEN count(a) > 0 THEN true ELSE false END FROM Account a where a.accountCode = :account")
    Boolean existsByAccount(@Param("account") final String accountCode);

}

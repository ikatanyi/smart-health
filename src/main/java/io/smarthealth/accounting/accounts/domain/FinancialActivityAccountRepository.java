package io.smarthealth.accounting.accounts.domain;

import io.smarthealth.accounting.accounts.data.FinancialActivity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface FinancialActivityAccountRepository extends JpaRepository<FinancialActivityAccount, Long> {

    Optional<FinancialActivityAccount> findByAccount(Account account);

    Optional<FinancialActivityAccount> findByFinancialActivity(FinancialActivity activity);

}

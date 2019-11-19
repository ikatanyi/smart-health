package io.smarthealth.accounting.account.domain;

import io.smarthealth.accounting.account.domain.enumeration.FinancialActivity;
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

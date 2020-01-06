package io.smarthealth.accounting.acc.domain;

import io.smarthealth.accounting.acc.data.v1.FinancialActivity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface FinancialActivityAccountRepository extends JpaRepository<FinancialActivityAccount, Long> {

    Optional<FinancialActivityAccount> findByAccount(AccountEntity account);
    
    Optional<FinancialActivityAccount> findByFinancialActivity(FinancialActivity activity);
    
}

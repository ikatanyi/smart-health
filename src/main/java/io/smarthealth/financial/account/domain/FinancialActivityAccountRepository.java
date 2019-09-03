package io.smarthealth.financial.account.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface FinancialActivityAccountRepository extends JpaRepository<FinancialActivityAccount, Long>{
    List<FinancialActivityAccount> findByAccount(Account account);
}

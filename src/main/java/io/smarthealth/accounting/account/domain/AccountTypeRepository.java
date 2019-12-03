package io.smarthealth.accounting.account.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface AccountTypeRepository extends JpaRepository<AccountType, Long>{
   Optional<AccountType> findByTypeIgnoreCase(String type);
}

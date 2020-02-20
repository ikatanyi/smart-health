package io.smarthealth.accounting.cashier.domain;

import io.smarthealth.security.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface CashierRepository extends JpaRepository<Cashier, Long> {

    Optional<Cashier> findByUser(User user);

}

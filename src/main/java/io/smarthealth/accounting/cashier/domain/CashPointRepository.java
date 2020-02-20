package io.smarthealth.accounting.cashier.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface CashPointRepository extends JpaRepository<CashPoint, Long> {
    Optional<CashPoint> findByName(String name);
}

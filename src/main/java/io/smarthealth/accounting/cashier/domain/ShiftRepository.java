package io.smarthealth.accounting.cashier.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByStatus(ShiftStatus status);

    List<Shift> findByCashier(Cashier cashier);

    Optional<Shift> findByStatusAndCashier(ShiftStatus status, Cashier cashier);
}

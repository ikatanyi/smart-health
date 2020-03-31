package io.smarthealth.accounting.cashier.domain;

import io.smarthealth.accounting.cashier.data.CashierShift;
import io.smarthealth.security.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Kelsas
 */
public interface CashierRepository extends JpaRepository<Cashier, Long> {

    Optional<Cashier> findByUser(User user);

    @Query("SELECT R.shift.cashPoint.name AS cashPoint,R.shift.cashier.user.name as cashier, R.shift.startDate as startDate,  R.shift.endDate as endDate,  R.shift.shiftNo as shiftNo,  R.shift.status as status,  SUM(R.amount - R.refundedAmount) as balance  FROM Receipt as R GROUP BY R.shift.shiftNo ORDER BY R.transactionDate DESC")
    List<CashierShift> shiftBalanceByDateInterface();
    
}

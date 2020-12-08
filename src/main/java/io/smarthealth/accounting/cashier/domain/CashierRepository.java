package io.smarthealth.accounting.cashier.domain;

import io.smarthealth.accounting.cashier.data.CashierShift;
import io.smarthealth.security.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface CashierRepository extends JpaRepository<Cashier, Long> {

    Optional<Cashier> findByUser(User user);
    Optional<Cashier> findByUserAndActive(User user, final Boolean active);

    Page<Cashier> findByActive(Boolean active, Pageable page);

    @Query("SELECT R.shift.id AS id,R.shift.cashPoint.name AS cashPoint,R.shift.cashier.user.name as cashier, R.shift.startDate as startDate,  R.shift.endDate as endDate,  R.shift.shiftNo as shiftNo,  R.shift.status as status,  SUM(R.amount - R.refundedAmount) as amount,R.shift.cashier.id as cashierId  FROM Receipt as R GROUP BY R.shift.shiftNo ORDER BY R.transactionDate DESC")
    Page<CashierShift> shiftBalanceByDateInterface(Pageable page);

    @Query("SELECT R.shift.id AS id, R.shift.cashPoint.name AS cashPoint,R.shift.cashier.user.name as cashier, R.shift.startDate as startDate,  R.shift.endDate as endDate,  R.shift.shiftNo as shiftNo,  R.shift.status as status,  SUM(R.amount - R.refundedAmount) as amount,R.shift.cashier.id as cashierId  FROM Receipt as R where R.shift.status =:status GROUP BY R.shift.shiftNo ORDER BY R.transactionDate DESC")
    Page<CashierShift> shiftBalanceByDateInterface(@Param("status") ShiftStatus status, Pageable page);
}

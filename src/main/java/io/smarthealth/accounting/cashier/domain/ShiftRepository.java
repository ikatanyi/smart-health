package io.smarthealth.accounting.cashier.domain;

import io.smarthealth.accounting.cashier.data.ShiftPayment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Kelsas
 */
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByStatus(ShiftStatus status);

    List<Shift> findByCashier(Cashier cashier);

    Optional<Shift> findByStatusAndCashier(ShiftStatus status, Cashier cashier);

    Optional<Shift> findByCashierAndShiftNo(Cashier cashier, String shiftNo);

    Optional<Shift> findByShiftNo(String shiftNo);

    Page<Shift> findByStatus(ShiftStatus status, Pageable page);

    @Query(value = "SELECT distinct receipt.shift_id AS shiftId, shift.shift_no AS shiftNo,cashier.id AS cashierId, appuser.username AS cashier, appuser.name AS cashierName , trans.method, SUM(trans.amount) over (PARTITION BY receipt.shift_id, trans.method ORDER BY receipt.shift_id ) as total  FROM acc_receipts receipt JOIN acc_receipt_transaction trans ON receipt.id=trans.receipt_id JOIN acc_cashiers_shifts shift ON shift.id=receipt.shift_id JOIN acc_cashiers cashier ON cashier.id=shift.cashier_id JOIN auth_user appuser ON appuser.id = cashier.user_id WHERE shift.shift_no=:shiftNo", nativeQuery = true)
    List<ShiftPayment> findShiftSummaryInterface(String shiftNo);
    
}

package io.smarthealth.accounting.payment.domain;

import io.smarthealth.accounting.cashier.data.CashierShift;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long>, JpaSpecificationExecutor<ReceiptItem> {

    @Query("SELECT "
            + "c.item.servicePoint AS cashPoint, "
            + "c.receipt.shift.cashier.user.name AS Cashier,"
            + " c.receipt.shift.startDate AS startDate, "
            + "c.receipt.shift.startDate AS endDate, "
            + "c.receipt.shift.shiftNo AS shiftNo,"
            + " c.receipt.shift.status AS shiftStatus,"
            + " SUM(c.amountPaid) AS amount, "
            + "c.receipt.shift.cashier.id AS cashierId  "
            + "FROM ReceiptItem AS c WHERE (:ShiftNo=null OR c.receipt.shift.shiftNo=:ShiftNo) AND c.receipt.paymentMethod!='Insurance' AND (:cashierId=null OR c.receipt.shift.cashier.id=:cashierId) GROUP BY c.item.servicePoint ORDER BY DATE(c.receipt.transactionDate) DESC")
    List<CashierShift> findTotalByCashierShift(@Param("ShiftNo") String ShiftNo, @Param("cashierId") Long cashierId);

    }

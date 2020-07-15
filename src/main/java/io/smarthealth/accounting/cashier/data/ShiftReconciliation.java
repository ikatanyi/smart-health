package io.smarthealth.accounting.cashier.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ShiftReconciliation {

    private String shiftNo;

    private Long cashierId;
    private String cashier;

    private Long cashPointId;
    private String cashPoint;

    private LocalDate dateReconciliation;
    private BigDecimal amount;
    private BigDecimal amountCollected;

    private Long depositAccountId;
    private String depositAccountNumber;
    private String depositAccountName;
    private String receivedBy;

    private Long expenseAccountId;
    private String expenseAccountNumber;
    private String expenseAccountName;
    private String narration;

}

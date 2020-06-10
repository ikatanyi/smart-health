package io.smarthealth.accounting.cashier.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    private Long depositAccountId;
    private String depositAccountNumber;
    private String depositAccountName;
    private String receivedBy;

    private Long expenseAccountId;
    private String expenseAccountNumber;
    private String expenseAccountName;
    private String narration;

    private List<ShiftPayment> shiftPayment = new ArrayList<>();
}

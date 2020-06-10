package io.smarthealth.accounting.cashier.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ShiftPayment {

    private String shiftNo;
    private Long paymentId;
    private String paymentMethod;
    private BigDecimal amount;
    private BigDecimal amountCollected;

}

package io.smarthealth.accounting.billing.data.nue;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillPayment {

    public enum Type {
        Copayment,
        Receipt
    } 
    private Type type;
    private String reference;
    private BigDecimal amount;

}

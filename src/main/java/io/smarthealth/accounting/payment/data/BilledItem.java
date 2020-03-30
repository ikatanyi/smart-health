package io.smarthealth.accounting.payment.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class BilledItem {

    private Long billItemId; //this can be the 
    private BigDecimal amount;
}

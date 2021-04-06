package io.smarthealth.accounting.payment.data;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Kelsas
 */
@Getter
@Setter
public class BillReceiptedItem {

    private Long billItemId; //this can be the 
    private Double quantity;
    private Double price;
    private BigDecimal discount;
    private BigDecimal subTotal;
    private BigDecimal taxes;
    private BigDecimal amount;
    private Long pricelistItemId;
    private Long servicePointId;
    private String servicePoint;
    private Long medicId;
    private Long doctorRequestId;

}

package io.smarthealth.accounting.billing.data.nue;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import  io.smarthealth.accounting.payment.domain.enumeration.ReceiptType;
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
    private ReceiptType receiptType;
}

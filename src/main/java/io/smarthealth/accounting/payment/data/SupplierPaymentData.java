package io.smarthealth.accounting.payment.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class SupplierPaymentData {
    private LocalDate invoiceDate;
    private String invoiceNumber;
    private BigDecimal invoiceAmount;
    private BigDecimal amountPaid;
    private BigDecimal taxAmount;
}

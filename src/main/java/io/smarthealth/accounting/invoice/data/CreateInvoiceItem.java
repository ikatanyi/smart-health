package io.smarthealth.accounting.invoice.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class CreateInvoiceItem {

    private Long billItemId;
    private BigDecimal amount;
    private String servicePoint;
    private Long servicePointId;
}

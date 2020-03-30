package io.smarthealth.accounting.invoice.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class CreateInvoiceItemData {

    private Long billItemId;
    private Double amount;
    private String servicePoint;
    private Long servicePointId;
}

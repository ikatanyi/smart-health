package io.smarthealth.accounting.invoice.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class LineItemData {

    private Long id;
    private String invoiceNumber;
    private Long itemId;
}

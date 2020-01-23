package io.smarthealth.debtor.claim.processing.data;

import java.io.Serializable;
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

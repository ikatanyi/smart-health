package io.smarthealth.stock.purchase.data;

import io.smarthealth.stock.purchase.domain.*;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class PurchaseCreditNoteItemData {

    private Long itemId;
    private String item;
    private String itemCode;
    private Double quantity;
    private BigDecimal rate;
    private BigDecimal tax;
    private BigDecimal amount;

}

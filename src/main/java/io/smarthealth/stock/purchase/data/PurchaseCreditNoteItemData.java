package io.smarthealth.stock.purchase.data;

import lombok.Data;

import java.math.BigDecimal;

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

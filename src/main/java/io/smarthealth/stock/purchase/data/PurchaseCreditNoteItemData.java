package io.smarthealth.stock.purchase.data;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @author Kelsas
 */
@Data
public class PurchaseCreditNoteItemData {
    private Long id;
    private Long itemId;
    private Long stockEntryId;
    private String item;
    private String itemCode;
    private Double quantity;
    private BigDecimal rate;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal amount;
    private Long storeId;

    public BigDecimal getExclusiveTotal(){

        if(quantity== null) quantity = 1D;
        if(rate== null) rate = BigDecimal.ZERO;
        if(discount== null) discount = BigDecimal.ZERO;

        BigDecimal qty = BigDecimal.valueOf(quantity);
        return (rate.multiply(qty)).subtract(discount);
    }

    public BigDecimal getTotal(){
        if(tax== null) tax = BigDecimal.ZERO;
        return getExclusiveTotal().add(tax);
    }
}

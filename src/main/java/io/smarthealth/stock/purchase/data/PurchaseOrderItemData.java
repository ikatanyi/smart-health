/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.purchase.data;

import io.smarthealth.stock.purchase.domain.PurchaseOrderItem;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class PurchaseOrderItemData {

    private Long purchaseOrderId;
    private Long itemId;
    private String itemCode;
    private String item;
    private double quantity;
    private double receivedQuantity;
    private BigDecimal price; //this can be linked to the pricelist and be defined in the values that
    private BigDecimal amount;

    public static PurchaseOrderItemData map(PurchaseOrderItem orderItem) {
        PurchaseOrderItemData data = new PurchaseOrderItemData();
        if (orderItem.getItem() != null) {
            data.setItemId(orderItem.getItem().getId());
            data.setItemCode(orderItem.getItem().getItemCode());
            data.setItem(orderItem.getItem().getItemName());
        }
        data.setPurchaseOrderId(orderItem.getId());
        data.setQuantity(orderItem.getQuantity());
        data.setReceivedQuantity(orderItem.getReceivedQuantity());
        data.setPrice(orderItem.getPrice());
        data.setAmount(orderItem.getAmount());
        return data;
    }
}

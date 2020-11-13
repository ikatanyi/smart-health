/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.purchase.data;

import io.smarthealth.stock.purchase.domain.PurchaseOrderItem;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    @ApiModelProperty(hidden=true)
    private String supplier;
    @ApiModelProperty(hidden=true)
    private String orderNumber;
    @ApiModelProperty(hidden=true)
    private LocalDate orderDate;
    @ApiModelProperty(hidden=true)
    private Double balance;

    public static PurchaseOrderItemData map(PurchaseOrderItem orderItem) {
        PurchaseOrderItemData data = new PurchaseOrderItemData();
        if (orderItem.getItem() != null) {
            data.setItemId(orderItem.getItem().getId());
            data.setItemCode(orderItem.getItem().getItemCode());
            data.setItem(orderItem.getItem().getItemName());
            
        }
        if(orderItem.getPurchaseOrder()!=null){
            data.setOrderNumber(orderItem.getPurchaseOrder().getOrderNumber());
            data.setOrderDate(orderItem.getPurchaseOrder().getTransactionDate());
            if(orderItem.getPurchaseOrder().getSupplier()!=null)
               data.setSupplier(orderItem.getPurchaseOrder().getSupplier().getSupplierName());
        }
        data.setBalance(orderItem.getQuantity()-orderItem.getReceivedQuantity());
        data.setPurchaseOrderId(orderItem.getId());
        data.setQuantity(orderItem.getQuantity());
        data.setReceivedQuantity(orderItem.getReceivedQuantity());
        data.setPrice(orderItem.getPrice());
        data.setAmount(orderItem.getAmount());
        return data;
    }
}

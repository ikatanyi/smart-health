/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.inventory.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class QuantityCountData {

    private Long itemId;
    private String itemCode;
    private Double quantityBalance; //the current available balance old balance
    private Double quantityCounted;//the new balance - the actual available
    private double quantityAdjusted; //(new balance - old balance)
    private String reasons;
}

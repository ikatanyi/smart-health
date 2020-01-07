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
    private Double quantityBalance;
    private Double quantityCounted;
    private double quantityAdjusted;

}

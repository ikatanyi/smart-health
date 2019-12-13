/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.item.data;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @author Kelsas
 */
@Data
public class ItemPriceData {

    private Long itemId;
    private BigDecimal rate;
    private Boolean enabled;
}

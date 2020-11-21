/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pricelist.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class BulkPriceUpdate {

    private Long itemId;
    private String itemCode;
    private String itemName;
    private BigDecimal amount;
    private Long[] pricebooks;
}

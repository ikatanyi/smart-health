/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.imports.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author kennedy.Ikatanyi
 */
@Data
public class InventoryStockData {

    private String itemCode;
    private Double stockCount;
    private Long storeId;
}

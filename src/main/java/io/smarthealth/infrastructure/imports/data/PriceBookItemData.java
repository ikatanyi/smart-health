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
 * @author Simon.waweru
 */
@Data
public class PriceBookItemData {

    private String itemCode;
    private String itemName;
    private Long itemId;
    private String priceBookName;
    private BigDecimal amount;
}

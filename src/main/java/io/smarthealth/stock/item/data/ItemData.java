/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.item.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class ItemData {

    private String itemCode;
    private String itemName;
    private String itemGroupName;
    private Long uomId;
    private BigDecimal sellingRate;
    private Boolean fixedAsset;
    private String description;
    private String barcode;
    private Integer shelfLife;
    private Boolean enabled;
}

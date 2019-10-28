package io.smarthealth.stock.item.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class CreateItem {

    private String itemType;
    private String itemName;
    private String sku;
    private double rate;
    private String itemUnit;
    private String description;
    private Long taxId; //tax
    private double purchaseRate; //cost rate
    private String stockCategory;
    private String drugCategory;
    private String drugStrength;
    private String drugRoute;
    private String doseForm;
    private Long inventoryStore;
    private Integer stockBalance;
    private Integer reorderLevel;
    private Integer orderQuantity;

}

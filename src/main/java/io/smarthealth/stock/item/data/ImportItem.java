package io.smarthealth.stock.item.data;

import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kelsas
 */
@Data
public class ImportItem {

    private ItemType itemType;
    private ItemCategory itemCategory;
    private String itemName;
     private String sku;
    private String description;
    private BigDecimal sellingPrice;
    private String itemUnit;

    private Long taxId; //tax
    private String taxName;
    private BigDecimal purchaseRate; //cost rate

    private String drugCategory;
    private String drugStrength;
    private String drugRoute;
    private String doseForm;
    private Long inventoryStore;
    private BigDecimal stockRatePerUnit;
    private Integer stockBalance;
    private Long reorderLevelId;
    private Double reorderLevel;
    private Double orderQuantity;
    private List<Long> expenseTo = new ArrayList<>();

}

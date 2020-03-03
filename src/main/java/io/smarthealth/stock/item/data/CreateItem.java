package io.smarthealth.stock.item.data;

import io.smarthealth.administration.servicepoint.data.SimpleServicePoint;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class CreateItem {

    private ItemType itemType;
    private String itemName;
    private String sku;
    private BigDecimal rate;
    private String itemUnit;
    private String description;
    private Long taxId; //tax
    private BigDecimal purchaseRate; //cost rate
    private ItemCategory stockCategory;
    private String drugCategory;
    private String drugStrength;
    private String drugRoute;
    private String doseForm;
    private Long inventoryStore;
    private BigDecimal stockRatePerUnit;
    private Integer stockBalance;
    private Integer reorderLevel;
    private Integer orderQuantity; 
    private List<Long> expenseTo = new ArrayList<>();
}

package io.smarthealth.stock.item.data;

import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @author Kelsas
 */
@Data
public class ItemData {

    private ItemType itemType;
    private ItemCategory category;

    private Long itemId;
    private String itemCode;
    private String itemName;
    private String unit;
    private String description;

    private BigDecimal rate;
    private BigDecimal costRate;
    
    private Boolean discountable;
    private Boolean taxable;
    private Boolean billable;
    
    private String drugCategory;
    private String strength;
    private String route;
    private String drugForm;
    private Boolean drug;
     
    private Long taxId;
    private String tax;
    private Boolean active;
    private Boolean generalFeeItem;

}

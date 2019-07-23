package io.smarthealth.stock.item.domain;

import io.smarthealth.stock.inventory.domain.ReorderRule;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *  An Item is a product or a service offered by the company.
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_item")
public class Item {
 
    public enum Group {
        Services,
        Consumable,
        Drug,
        Stock
    }
    private String itemCode;
    private String itemName;
    @Enumerated(EnumType.STRING)
    private Group itemGroup;
    private Uom uom;
    private BigDecimal sellingRate;
    private Boolean fixedAsset; 
    private String description; 
    private String barcode;
    private Integer shelfLife; 
    private Boolean enabled;
    
    @OneToMany(mappedBy = "item")
    private List<ItemPrice> itemPrice;
    
    //reorder levels'
    @OneToMany(mappedBy = "stockItem")
    private List<ReorderRule> reorderRules=new ArrayList<>();

}

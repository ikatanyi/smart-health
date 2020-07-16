package io.smarthealth.stock.item.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.smarthealth.accounting.pricelist.domain.PriceBookItem;
import io.smarthealth.accounting.pricelist.domain.PriceList;
import io.smarthealth.accounting.taxes.domain.Tax;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.data.ItemData;
import io.smarthealth.stock.item.data.SimpleItemData;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 * Product or Service representation
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "product_services")
public class Item extends Identifiable {
 
    @Enumerated(EnumType.STRING)
    private ItemType itemType;
    @Enumerated(EnumType.STRING)
    private ItemCategory category;
    private String itemName;
    private String itemCode;//sku;
    private BigDecimal rate;
    private BigDecimal costRate;
    private Boolean discountable;
    private Boolean taxable;
    private Boolean billable;
    private String drugCategory;
    private String strength;
    private String route;
    private String drugForm;
    @Column(name = "is_drug")
    private Boolean drug;
    private String unit;
    private String description;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_item_tax_id"))
    private Tax tax;

    //this should be populated if it's a stock item
    @JsonIgnore
    @OneToMany(mappedBy = "stockItem")
    private List<ReorderRule> reorderRules;
    //sales, purchase, inventory accounts to be linked via the store
    @JsonIgnore
    @OneToMany(mappedBy = "priceBook", cascade = CascadeType.ALL)
    private Set<PriceBookItem> priceBookItems = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<PriceList> priceLists = new ArrayList<>();
    
    private Boolean active;

    public boolean isInventoryItem() {
        return this.itemType == ItemType.Inventory;
    }

       public void addPricelist(PriceList pricelist) {
        pricelist.setItem(this);
        priceLists.add(pricelist);
    }

    public void addPricelist(List<PriceList> pricelists) {
        this.priceLists=pricelists;
        this.priceLists.forEach(x -> x.setItem(this));
    }
    
    public ItemData toData() {
        ItemData data = new ItemData();
        data.setActive(this.active);
        data.setBillable(this.billable);
        data.setCategory(this.category);
        data.setCostRate(this.costRate);
        data.setDescription(this.description);
        data.setDiscountable(this.discountable);
        data.setDrug(this.drug);
        data.setDrugCategory(this.drugCategory);
        data.setDrugForm(this.drugForm);
        data.setItemCode(this.itemCode);
        data.setItemId(this.getId());
        data.setItemName(this.itemName);
        data.setItemType(this.itemType);
        data.setRate(this.rate);
        data.setRoute(this.route);
        data.setStrength(this.strength);
        if (this.tax != null) {
            data.setTax(this.tax.getTaxName());
            data.setTaxId(this.tax.getId());
        }
        data.setUnit(this.unit);
        return data;
    }

    public SimpleItemData toSimpleData() {
        SimpleItemData data = new SimpleItemData();
        data.setItemCode(this.itemCode);
        data.setItemId(this.getId());
        data.setItemName(this.itemName);
        return data;
    }
    
    @Override
    public String toString() {
        return "Item [id=" + getId() + ", name=" + itemName + ", code=" + itemCode + ", type=" + itemType + "]";
    }
}



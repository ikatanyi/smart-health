package io.smarthealth.stock.item.domain;

import io.smarthealth.accounting.pricebook.domain.PriceBook;
import io.smarthealth.accounting.taxes.domain.Tax;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

/**
 * Product or Service representation
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_item_service",uniqueConstraints = {
    @UniqueConstraint(columnNames = {"category","itemName"})})
public class Item extends Identifiable {

    private String itemType;
    private String category; // is this a consumable, service, procedure, inventory
    private String itemName;
    private String itemCode;//sku;
    private double rate;
    private double costRate;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_item_uom"))
    private Uom uom;
    private String description;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_item_tax_id"))
    private Tax tax;

    //this should be populated if it's a stock item
    @OneToMany(mappedBy = "stockItem")
    private List<ReorderRule> reorderRules;
    //sales, purchase, inventory accounts to be linked via the store
    @ManyToMany(mappedBy = "priceBookItems")
    private List<PriceBook> priceBooks;
    private Boolean active;

    public boolean isInventoryItem() {
        return this.itemType.equals("Inventory");
    }
}

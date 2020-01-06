package io.smarthealth.stock.inventory.domain;

import io.smarthealth.accounting.acc.domain.AccountEntity;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.inventory.domain.enumeration.ModeofAdjustment;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

/**
 *  Captures the Inventory Variance quantity and reasons
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_inventory_variance")
public class InventoryVariance extends Auditable {

    private LocalDateTime dateRecorded;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_variance_store_id"))
    private Store store;
    private String description; 
    @ManyToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_variance_item_id"))
    private AccountEntity costingAccount;
    @Enumerated(EnumType.STRING)
    private ModeofAdjustment adjustmentMode;
    private String reference;
    
    @OneToMany
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_inventory_variance_var_item_id"))
    private List<VarItem>varItem; 
    
    //use code - Stock Variance Reason
}

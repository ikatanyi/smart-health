package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Balance Transaction Line of a given {@link Item } . It holds the current
 * balance information
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_inventory_item")
public class InventoryItem extends Identifiable {

    public enum StatusType {
        Good,
        Expired
    }
    @ManyToOne
    private Department store;
    @ManyToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_inventory_item_id"))
    private Item item;
    private double quantity;
    @Enumerated(EnumType.STRING)
    private StatusType statusType;
    private String itemType;
    private String serialNumber;
    private LocalDateTime dateRecorded;

}

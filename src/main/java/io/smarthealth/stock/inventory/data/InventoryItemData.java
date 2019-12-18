package io.smarthealth.stock.inventory.data;

import io.smarthealth.stock.inventory.domain.*;
import io.smarthealth.stock.inventory.domain.enumeration.StatusType;
import io.smarthealth.stock.item.data.ItemData;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.data.StoreData;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 * Balance Transaction Line of a given {@link Item } . It holds the current
 * balance information
 *
 * @author Kelsas
 */
@Data
public class InventoryItemData {
 
    private Long storeId;
    private Long id;
    private Long itemId;
    private StoreData storeData;
    private ItemData itemData;
    private double quantity;
    @Enumerated(EnumType.STRING)
    private StatusType statusType;
    private String itemType;
    private String serialNumber;
    private LocalDateTime dateRecorded;
    
    

    public static InventoryItem map(InventoryItemData data){
        InventoryItem entity = new InventoryItem();
        entity.setDateRecorded(data.getDateRecorded());
        entity.setItemType(data.getItemType());
        entity.setQuantity(data.getQuantity());
        entity.setSerialNumber(data.getSerialNumber());
        entity.setStatusType(data.getStatusType());
        return entity;
    }
    //
}

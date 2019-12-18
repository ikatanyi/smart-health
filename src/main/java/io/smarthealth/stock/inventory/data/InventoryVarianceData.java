package io.smarthealth.stock.inventory.data;

import io.smarthealth.stock.inventory.domain.*;
import io.smarthealth.stock.item.data.ItemData;
import io.smarthealth.stock.stores.data.StoreData;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *  Captures the Inventory Variance quantity and reasons
 *
 * @author Kelsas
 */
@Data
public class InventoryVarianceData {

    private LocalDateTime dateRecorded;
    private StoreData storeData;
    private ItemData itemData;
    private double quantity;
    private String comments; 
    private String reasons;
    private Long storeId;
    private Long itemId;
    //use code - Stock Variance Reason
    
    public static InventoryVariance map(InventoryVarianceData data){
        InventoryVariance variance = new InventoryVariance();
        variance.setComments(data.getComments());
        variance.setQuantity(data.getQuantity());
        variance.setReasons(data.getReasons());
        variance.setDateRecorded(LocalDateTime.now());
        return variance;
    }
}

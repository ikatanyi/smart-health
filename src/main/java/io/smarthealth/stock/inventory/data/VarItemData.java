package io.smarthealth.stock.inventory.data;

import io.smarthealth.stock.inventory.domain.VarItem;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class VarItemData {
    private Long id;    
    private String itemName;    
    private String reasons;
    private Integer quantity;
    private Integer variance;
    
    
    public static VarItem map(VarItemData data){
        VarItem vItem = new VarItem();
        vItem.setId(data.getId());
        vItem.setQuantity(data.getQuantity());
        vItem.setReason(data.getReasons());
        vItem.setVariance(data.getVariance());
        return vItem;
    }
}

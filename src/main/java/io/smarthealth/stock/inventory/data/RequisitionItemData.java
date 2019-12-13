package io.smarthealth.stock.inventory.data;

import io.smarthealth.stock.inventory.domain.RequisitionItem;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class RequisitionItemData {

    private Long requisitionId;
    private Long itemId;
    private String itemCode;
    private String item;
    private double quantity;
    private double receivedQuantity;
    private Long uomId;
    private String uom;

    public static RequisitionItemData map(RequisitionItem item) {
        RequisitionItemData data = new RequisitionItemData();
        if (item.getItem() != null) {
            data.setItemId(item.getItem().getId());
            data.setItemCode(item.getItem().getItemCode());
            data.setItem(item.getItem().getItemName());
        }
        data.setQuantity(item.getQuantity());
        data.setReceivedQuantity(item.getReceivedQuantity());
        if(item.getUom()!=null){
            data.setUomId(item.getUom().getId());
            data.setUom(item.getUom().getName());
        }

        return data;
    }
}

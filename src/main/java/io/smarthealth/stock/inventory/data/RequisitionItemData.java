package io.smarthealth.stock.inventory.data;

import io.smarthealth.stock.inventory.domain.RequisitionItem;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class RequisitionItemData {

    private Long id;
    private Long itemId;
    private String itemCode;
    private String item;
    private double quantity;
    private double receivedQuantity;
    private double price;
    private double total;
    private String uom;

    public static RequisitionItemData map(RequisitionItem item) {
        RequisitionItemData data = new RequisitionItemData();
        data.setId(item.getId());
        if (item.getItem() != null) {
            data.setItemId(item.getItem().getId());
            data.setItemCode(item.getItem().getItemCode());
            data.setItem(item.getItem().getItemName());
        }
        data.setPrice(item.getPrice());
        data.setQuantity(item.getQuantity());
        data.setReceivedQuantity(item.getReceivedQuantity());

        return data;
    }
}

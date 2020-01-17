package io.smarthealth.stock.item.data;

import io.smarthealth.stock.item.domain.Item;
import lombok.Data;
/**
 *
 * @author Kelsas
 */
@Data
public class ItemSimpleData {

    private Long itemId;
    private String itemType;
    private String itemName;
    private String itemCode;
    private  Double price;

    public static ItemSimpleData map(Item item) {
        ItemSimpleData data = new ItemSimpleData();
        data.setItemId(item.getId());
        data.setItemName(item.getItemName());
        data.setItemType(item.getItemType());
        data.setItemCode(item.getItemCode());
        data.setPrice(Double.NaN);
        return data;
    }
}

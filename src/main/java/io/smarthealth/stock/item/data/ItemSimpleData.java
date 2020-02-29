package io.smarthealth.stock.item.data;

import io.smarthealth.accounting.pricelist.domain.PriceBookItem;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ItemSimpleData {

    private Long itemId;
    private ItemType itemType;
    private String itemName;
    private String itemCode;
    private BigDecimal amount;
    private BigDecimal defaultAmount;

    public static ItemSimpleData map(PriceBookItem bookItem) {
        ItemSimpleData data = new ItemSimpleData();
        data.setItemId(bookItem.getItem().getId());
        data.setItemName(bookItem.getItem().getItemName());
        data.setItemType(bookItem.getItem().getItemType());
        data.setItemCode(bookItem.getItem().getItemCode());
        data.setAmount(bookItem.getAmount());
        data.setDefaultAmount(bookItem.getItem().getRate());
        return data;
    }
}

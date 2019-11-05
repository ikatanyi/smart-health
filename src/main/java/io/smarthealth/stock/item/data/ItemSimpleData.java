/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    public static ItemSimpleData map(Item item) {
        ItemSimpleData data = new ItemSimpleData();
        data.setItemId(item.getId());
        data.setItemName(item.getItemName());
        data.setItemType(item.getItemType());
        return data;
    }
}

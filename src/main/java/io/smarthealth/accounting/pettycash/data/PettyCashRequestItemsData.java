/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.data;

import io.smarthealth.accounting.pettycash.domain.PettyCashRequestItems;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PettyCashRequestItemsData {
    
    private Long itemId;
    private String item;
    private double pricePerUnit;
    private int quantity;
    
    @ApiModelProperty(hidden = true)
    private double amount;
    private String narration;
    
    @ApiModelProperty(hidden = true)
    private String requestNo;
    
    public static PettyCashRequestItemsData map(PettyCashRequestItems e) {
        PettyCashRequestItemsData data = new PettyCashRequestItemsData();
        data.setItemId(e.getId());
        data.setAmount(e.getAmount());
        data.setItem(e.getItem());
        data.setNarration(e.getNarration());
        data.setPricePerUnit(e.getPricePerUnit());
        data.setQuantity(e.getQuantity());
        data.setRequestNo(e.getRequestNo().getRequestNo());
        return data;
    }
    
    public static List<PettyCashRequestItemsData> map(List<PettyCashRequestItems> e) {
        List<PettyCashRequestItemsData> itemData = new ArrayList<>();
        for (PettyCashRequestItems entity : e) {
            itemData.add(map(entity));
        }
        return itemData;
    }
    
    public static PettyCashRequestItems map(PettyCashRequestItemsData data) {
        PettyCashRequestItems e = new PettyCashRequestItems();
        e.setAmount(data.getAmount());
        e.setItem(data.getItem());
        e.setNarration(data.getNarration());
        e.setPricePerUnit(data.getPricePerUnit());
        e.setQuantity(data.getQuantity());
        return e;
    }
}

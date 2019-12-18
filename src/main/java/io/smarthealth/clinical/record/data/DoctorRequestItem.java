/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import io.smarthealth.clinical.record.domain.DoctorRequest;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class DoctorRequestItem {
    
    private Long itemId;
    private String itemName;
    private String code;
    private double costRate;
    private double rate;
    private Long requestItemId;

    public static DoctorRequestItem map(DoctorRequest d) {
        DoctorRequestItem requestItem = new DoctorRequestItem();
        requestItem.setCode(d.getItem().getItemCode());
        requestItem.setCostRate(d.getItemCostRate());
        requestItem.setItemId(d.getItem().getId());
        requestItem.setItemName(d.getItem().getItemName());
        requestItem.setRate(d.getItemRate());
        requestItem.setRequestItemId(d.getId());
        return requestItem;
    }
}

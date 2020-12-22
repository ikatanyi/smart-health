/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.data;

import io.smarthealth.accounting.pettycash.data.enums.PettyCashStatus;
import io.smarthealth.accounting.pettycash.domain.PettyCashApprovedItems;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PettyCashApprovedItemsData {
    
    @Enumerated(EnumType.STRING)
    private PettyCashStatus approvalStatus;
    
    private String approvalComments;
    private String itemName;
    
    private Long itemNo;
    
    private double pricePerUnit;
    private int quantity;
    private double amount;
    
    public static PettyCashApprovedItemsData map(PettyCashApprovedItems entity) {
        PettyCashApprovedItemsData data = new PettyCashApprovedItemsData();
        data.setApprovalComments(entity.getApprovalComments());
        data.setApprovalStatus(entity.getApprovalStatus());
        data.setItemNo(entity.getItemNo().getId());
        data.setPricePerUnit(entity.getPricePerUnit());
        data.setQuantity(entity.getQuantity());
        data.setAmount(entity.getAmount());
        data.setItemName(entity.getItemNo().getItem());
        return data;
    }
}

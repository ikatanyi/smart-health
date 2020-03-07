/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.data;

import io.smarthealth.clinical.radiology.domain.RadiologyTest;
import io.smarthealth.stock.item.data.SimpleItemData;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class RadiologyTestData {
    private Long id;
    private String itemCode;
    private String scanName; 
    private Boolean active; 
    private String notes;
    private Boolean supervisorConfirmation;
    @Enumerated(EnumType.STRING)
    private String gender;  
    private SimpleItemData itemData;
    
    public static RadiologyTestData map(RadiologyTest rtd){
        RadiologyTestData entity = new RadiologyTestData();
        entity.setId(rtd.getId());
        entity.setNotes(rtd.getNotes());
        entity.setScanName(rtd.getScanName());
        if(rtd.getItem()!=null)
           entity.setItemData(rtd.getItem().toSimpleData());
        entity.setActive(rtd.getStatus());     
        
        return entity;
    }
    
     public static RadiologyTest map(RadiologyTestData rtd){
        RadiologyTest entity = new RadiologyTest();
        entity.setNotes(rtd.getNotes());
        entity.setScanName(rtd.getScanName());
        entity.setStatus(rtd.getActive());
        
        return entity;
    }
}

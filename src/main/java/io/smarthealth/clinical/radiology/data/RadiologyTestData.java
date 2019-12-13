/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.data;

import io.smarthealth.clinical.lab.data.LabTestTypeData;
import io.smarthealth.clinical.radiology.domain.RadiologyTest;
import io.smarthealth.stock.item.data.ItemData;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class RadiologyTestData {
    private Long id;
    private String itemCode;
    private String scanName; //government classifications
    private Boolean consent; 
    private Boolean withRef; 
    private Boolean refOut; 
    private Boolean status; 
    private String notes;
    private Boolean supervisorConfirmation;
    @Enumerated(EnumType.STRING)
    private LabTestTypeData.Gender gender;  
    private ItemData itemData;
    
    public static RadiologyTestData map(RadiologyTest rtd){
        RadiologyTestData entity = new RadiologyTestData();
        entity.setConsent(rtd.getConsent());
        entity.setGender(rtd.getGender());
        entity.setNotes(rtd.getNotes());
        entity.setRefOut(rtd.getRefOut());
        entity.setScanName(rtd.getScanName());
        if(rtd.getItem()!=null)
           entity.setItemData(ItemData.map(rtd.getItem()));
        entity.setStatus(rtd.getStatus());
        entity.setSupervisorConfirmation(rtd.getSupervisorConfirmation());
        entity.setWithRef(rtd.getWithRef());
        
        
        return entity;
    }
    
     public static RadiologyTest map(RadiologyTestData rtd){
        RadiologyTest entity = new RadiologyTest();
        entity.setConsent(rtd.getConsent());
        entity.setGender(rtd.getGender());
        entity.setNotes(rtd.getNotes());
        entity.setRefOut(rtd.getRefOut());
        entity.setScanName(rtd.getScanName());
        entity.setStatus(rtd.getStatus());
        entity.setSupervisorConfirmation(rtd.getSupervisorConfirmation());
        entity.setWithRef(rtd.getWithRef());
        
        return entity;
    }
}

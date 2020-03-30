/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.data;


import io.smarthealth.clinical.radiology.domain.RadiologyTest;
import io.smarthealth.clinical.radiology.domain.enumeration.Category;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.smarthealth.stock.item.data.SimpleItemData;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class RadiologyTestData {
    @ApiModelProperty(required=false, hidden=true)
    private Long id;
    private String itemCode;
    private String scanName; 
    private Boolean active; 
    private String notes;
    private Long templateId;
    @ApiModelProperty(required=false, hidden=true)
    private String templateName;
    @ApiModelProperty(required=false, hidden=true)
    private String templateNotes;
    private Boolean supervisorConfirmation;
    @Enumerated(EnumType.STRING)
    private Gender gender;  
    @Enumerated(EnumType.STRING)
    private Category category;
    
    
    
     public RadiologyTest map(RadiologyTestData rtd){
        RadiologyTest entity = new RadiologyTest();
        entity.setNotes(rtd.getNotes());
        entity.setScanName(rtd.getScanName());
        entity.setStatus(rtd.getActive());
        entity.setCategory(this.getCategory());
        entity.setGender(this.getGender());
        entity.setSupervisorConfirmation(this.getSupervisorConfirmation());
        return entity;
    }
}

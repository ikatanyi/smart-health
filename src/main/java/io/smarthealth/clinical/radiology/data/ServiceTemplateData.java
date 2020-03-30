/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.data;

import io.smarthealth.clinical.radiology.domain.ServiceTemplate;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class ServiceTemplateData {

    @ApiModelProperty(required = false, hidden = true)
    private Long id;
    private String templateName;
    private Gender gender;
    private  String notes;

    public ServiceTemplate fromData() {
        ServiceTemplate entity = new ServiceTemplate();
        entity.setGender(this.getGender());
        if(notes!=null)
            entity.setNotes(this.getNotes().getBytes());
        entity.setTemplateName(this.getTemplateName());
        return entity;
    }
}

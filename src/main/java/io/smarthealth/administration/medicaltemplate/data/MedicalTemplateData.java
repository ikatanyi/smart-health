/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.medicaltemplate.data;

import io.smarthealth.administration.medicaltemplate.domain.MedicalTemplate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class MedicalTemplateData {

    @ApiModelProperty(required = false, hidden = true)
    private Long id;
    private String templateName;
    private String notes;

    public MedicalTemplate fromData() {
        MedicalTemplate entity = new MedicalTemplate();
        if (notes != null) {
            entity.setNotes(this.getNotes().getBytes());
        }
        entity.setTemplateName(this.getTemplateName());
        return entity;
    }
}

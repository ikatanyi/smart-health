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
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class ServiceTemplateData {

    @ApiModelProperty(required = false, hidden = true)
    private Long id;
    private MultipartFile templateFile;
    private String templateName;
    private Gender gender;
    private String notes;
    @ApiModelProperty(required = false, hidden = true)
    private String fileType;
    @ApiModelProperty(required = false, hidden = true)
    private Long size;
    @ApiModelProperty(required = false, hidden = true)
    private byte[] template;
    private String fileString;

    public ServiceTemplate fromData() {
        ServiceTemplate entity = new ServiceTemplate();
        entity.setGender(this.getGender());
        entity.setNotes(this.getNotes());
        if (this.getTemplate() != null) {
            entity.setTemplateName(this.getTemplateFile().getName());
            entity.setSize(this.getTemplateFile().getSize());
            entity.setFileType(this.getTemplateFile().getContentType());
        }
        return entity;
    }
}

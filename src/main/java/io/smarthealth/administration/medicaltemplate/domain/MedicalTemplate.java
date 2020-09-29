/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.medicaltemplate.domain;

import io.smarthealth.administration.medicaltemplate.data.MedicalTemplateData;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.nio.charset.StandardCharsets;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
@Entity
@Table(name = "medical_template", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"templateName"}, name = "unique_template_name")})
public class MedicalTemplate extends Identifiable {

    private String templateName;
    @Lob
    private byte[] notes;

    public MedicalTemplateData toData() {
        MedicalTemplateData data = new MedicalTemplateData();
        data.setTemplateName(this.getTemplateName());
        if (this.getNotes() != null) {
//            data.setNotes(new String(this.getNotes(), StandardCharsets.UTF_8));
            data.setNotes(new String(this.getNotes()));
        }
        data.setId(this.getId());
        return data;
    }
}

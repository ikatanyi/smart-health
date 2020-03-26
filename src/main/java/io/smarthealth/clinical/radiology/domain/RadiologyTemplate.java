/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Entity
@Data
public class RadiologyTemplate extends Auditable {

    
    private String templateName;
    private String description;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "template")
    private List<RadiologyTemplateNotes> templateNotes;

    public void addRadiologyTemplateNotes(RadiologyTemplateNotes templateNote) {
        templateNotes.add(templateNote);
        templateNote.setTemplate(this);
    }
}

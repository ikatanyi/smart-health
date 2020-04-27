/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import io.smarthealth.clinical.radiology.data.RadiologyTestData;
import io.smarthealth.clinical.radiology.domain.enumeration.Category;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.smarthealth.stock.item.domain.Item;
import java.nio.charset.StandardCharsets;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "radiology_tests", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"scanName", "item_id"}, name = "unique_scan_name_item_id")})
public class RadiologyTest extends Identifiable {

    private String scanName;
    private String code;
    private Boolean status;
    private Boolean supervisorConfirmation;
    private String notes;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @OneToOne
    private Item item;
    @OneToOne
    private ServiceTemplate serviceTemplate;
    @Enumerated(EnumType.STRING)
    private Category category;

    public RadiologyTestData toData() {
        RadiologyTestData entity = new RadiologyTestData();
        entity.setId(this.getId());
        entity.setNotes(this.getNotes());
        entity.setScanName(this.getScanName());
        entity.setSupervisorConfirmation(this.getSupervisorConfirmation());
        if (this.getServiceTemplate() != null) {
            entity.setTemplateName(this.getServiceTemplate().getTemplateName());
            entity.setTemplateId(this.getServiceTemplate().getId());
            if (this.getServiceTemplate().getNotes() != null) {
                entity.setTemplateNotes(new String(this.getServiceTemplate().getNotes(), StandardCharsets.UTF_8));
            }
        }
        if (this.getItem() != null) {
            entity.setItemCode(this.getItem().getItemCode());
        }

        entity.setActive(this.getStatus());
        entity.setCategory(this.getCategory());
        entity.setGender(this.getGender());

        return entity;
    }
}

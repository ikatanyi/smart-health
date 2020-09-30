/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.wardprocedure.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.wardprocedure.data.ConsentFormData;
import io.smarthealth.infrastructure.domain.Auditable;
import java.nio.charset.StandardCharsets;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
@Entity
public class ConsentForm extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_consent_visit_id"))
    private Visit visit;
    @Lob
    private byte[] notes;
    private String consentType;

    public ConsentFormData toData() {
        ConsentFormData d = new ConsentFormData();
        d.setConsentType(this.getConsentType());
        if (notes != null) {
//        d.setNotes(new String(this.notes, StandardCharsets.UTF_8));
            d.setNotes(new String(this.getNotes()));
        }
        return d;
    }

}

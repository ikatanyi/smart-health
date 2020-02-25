/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
@Entity
public class TriageNotes extends Auditable {

    private String bleeding,
            mentalStatus,
            LMP,
            dehydration, 
            cardex;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", foreignKey = @ForeignKey(name = "fk_triage_notes_visit_id"))
    private Visit visit;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Entity;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
@Entity
public class TriageNotes extends Auditable{

    private String bleeding,
            mentalStatus,
            LMP,
            dehydration, cardex;
}

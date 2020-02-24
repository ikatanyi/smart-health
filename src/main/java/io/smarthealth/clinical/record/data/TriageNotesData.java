/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class TriageNotesData {

    private String bleeding,
            mentalStatus,
            LMP,
            dehydration,
            cardex;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import io.smarthealth.clinical.record.domain.TriageNotes;
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

    public static TriageNotes map(TriageNotesData data) {
        TriageNotes note = new TriageNotes();
        note.setBleeding(data.getBleeding());
        note.setCardex(data.getCardex());
        note.setDehydration(data.getDehydration());
        note.setLMP(data.getLMP());
        note.setMentalStatus(data.getMentalStatus());
        return note;
    }

    public static TriageNotesData map(TriageNotes e) {
        TriageNotesData data = new TriageNotesData();
        data.setBleeding(e.getBleeding());
        data.setCardex(e.getCardex());
        data.setDehydration(e.getDehydration());
        data.setLMP(e.getLMP());
        data.setMentalStatus(e.getMentalStatus());
        return data;
    }
}

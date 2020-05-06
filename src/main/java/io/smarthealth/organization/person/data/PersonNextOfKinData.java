/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.data;

import io.smarthealth.organization.person.domain.PersonNextOfKin;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PersonNextOfKinData {

    private String name;
    private String relationship;
    private String specialNote;
    private String primaryContact;

    public static PersonNextOfKin map(final PersonNextOfKinData data) {
        PersonNextOfKin nok = new PersonNextOfKin();
        nok.setName(data.getName());
        nok.setPrimaryContact(data.getPrimaryContact());
        nok.setRelationship(data.getRelationship());
        nok.setSpecialNote(data.getSpecialNote());
        return nok;
    }

    public static PersonNextOfKinData map(final PersonNextOfKin e) {
        PersonNextOfKinData nok = new PersonNextOfKinData();
        nok.setName(e.getName());
        nok.setPrimaryContact(e.getPrimaryContact());
        nok.setRelationship(e.getRelationship());
        nok.setSpecialNote(e.getSpecialNote());
        return nok;
    }
}

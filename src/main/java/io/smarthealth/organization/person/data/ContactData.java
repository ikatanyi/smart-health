/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.data;

import io.smarthealth.organization.person.domain.PersonAddress;
import io.smarthealth.organization.person.domain.PersonContact;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public final class ContactData {

    private String email;
    private String telephone;
    private String mobile;

    public static ContactData map(PersonContact personContactEntity) {
        ContactData contactDTO = new ContactData();
        contactDTO.setEmail(personContactEntity.getEmail());
        contactDTO.setMobile(personContactEntity.getMobile());
        contactDTO.setTelephone(personContactEntity.getTelephone());
        return contactDTO;
    }

    public static PersonContact map(ContactData personContactDto) {
        PersonContact personContactEntity = new PersonContact();
        personContactEntity.setEmail(personContactDto.getEmail());
        personContactEntity.setMobile(personContactDto.getMobile());
        personContactEntity.setTelephone(personContactDto.getTelephone());
        return personContactEntity;
    }
}

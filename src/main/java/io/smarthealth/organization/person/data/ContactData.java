/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.organization.person.domain.PersonContact;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public final class ContactData {

    @ApiModelProperty(required = false, hidden = true)
    private Long id;

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

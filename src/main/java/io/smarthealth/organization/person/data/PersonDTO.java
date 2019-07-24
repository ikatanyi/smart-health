/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.data;

import io.smarthealth.organization.person.domain.Person;
import io.smarthealth.organization.person.domain.PersonAddress;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PersonDTO {
    
    private String title;
    private String givenName;
    private String middleName;
    private String surname;
    private String gender;
    private boolean isPatient;
    private LocalDate dateOfBirth;
    private List<AddressDTO> address;
    private List<ContactDTO> contact;
    private String maritalStatus;
    
    public static Person map(final PersonDTO personDTO) {
        Person person = new Person();
        person.setTitle(personDTO.getTitle());
        person.setGivenName(personDTO.getGivenName());
        person.setMiddleName(personDTO.getMiddleName());
        person.setSurname(personDTO.getSurname());
        person.setGender(personDTO.getGender());
        person.setPatient(personDTO.isPatient);
        person.setDateOfBirth(personDTO.getDateOfBirth());
        if (personDTO.getAddress() != null) {
            person.setAddresses(
                    personDTO.getAddress().stream()
                            .map(AddressDTO::map)
                            .collect(Collectors.toList())
            );
        }
        if (personDTO.getContact() != null) {
            person.setContacts(personDTO.getContact().stream().map(ContactDTO::map).collect(Collectors.toList()));
        }
        person.setMaritalStatus(personDTO.getMaritalStatus());
        return person;
    }
    
    public static PersonDTO map(final Person person) {
        PersonDTO persondto = new PersonDTO();
        persondto.setTitle(person.getTitle());
        persondto.setGivenName(person.getGivenName());
        persondto.setMiddleName(person.getMiddleName());
        persondto.setSurname(person.getSurname());
        persondto.setGender(person.getGender());
        persondto.setPatient(person.isPatient());
        persondto.setDateOfBirth(person.getDateOfBirth());
        if (person.getAddresses() != null) {
            persondto.setAddress(
                    person.getAddresses().stream()
                            .map(AddressDTO::map)
                            .collect(Collectors.toList())
            );
        }
        if (person.getContacts() != null) {
            persondto.setContact(person.getContacts().stream().map(ContactDTO::map).collect(Collectors.toList()));
        }
        person.setMaritalStatus(person.getMaritalStatus());
        return persondto;
    }
}

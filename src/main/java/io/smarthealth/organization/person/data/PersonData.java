/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import io.smarthealth.organization.person.domain.Person;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.smarthealth.organization.person.domain.enumeration.MaritalStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PersonData {

    private String title;

    @NotBlank
    private String givenName;
    private String middleName;
    @NotBlank
    private String surname;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private Gender  gender;
    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private MaritalStatus  maritalStatus;
    private List<AddressData> address;
    private List<ContactData> contact;
    private String fullName;

    private boolean isPatient;

    public static Person map(final PersonData personDTO) {
        Person person = new Person();
        person.setTitle(personDTO.getTitle());
        person.setGivenName(personDTO.getGivenName());
        person.setMiddleName(personDTO.getMiddleName());
        person.setSurname(personDTO.getSurname());
        person.setGender(personDTO.getGender().name());
        person.setPatient(personDTO.isPatient);
        person.setDateOfBirth(personDTO.getDateOfBirth());
        if (personDTO.getAddress() != null) {
            person.setAddresses(
                    personDTO.getAddress().stream()
                            .map(AddressData::map)
                            .collect(Collectors.toList())
            );
        }
        if (personDTO.getContact() != null) {
            person.setContacts(personDTO.getContact().stream().map(ContactData::map).collect(Collectors.toList()));
        }
        person.setMaritalStatus(personDTO.getMaritalStatus().name());
        return person;
    }

    public static PersonData map(final Person person) {
        PersonData persondto = new PersonData();
        persondto.setTitle(person.getTitle());
        persondto.setGivenName(person.getGivenName());
        persondto.setMiddleName(person.getMiddleName());
        persondto.setSurname(person.getSurname());
        persondto.setPatient(person.isPatient());
        persondto.setDateOfBirth(person.getDateOfBirth());
        if (person.getAddresses() != null) {
            persondto.setAddress(
                    person.getAddresses().stream()
                            .map(AddressData::map)
                            .collect(Collectors.toList())
            );
        }
        if (person.getContacts() != null) {
            persondto.setContact(person.getContacts().stream().map(ContactData::map).collect(Collectors.toList()));
        }
        person.setMaritalStatus(person.getMaritalStatus());
        return persondto;
    }
}

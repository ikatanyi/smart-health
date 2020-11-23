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
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
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
    private Gender gender;
    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private MaritalStatus maritalStatus;
    private List<AddressDatas> address;
    private List<ContactData> contact;
    private List<PersonNextOfKinData> nok;
    private String fullName;
    private LocalDate createdOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate dateRegistered=LocalDate.now();
    private String phone;
    private String addressLine;

    private boolean isPatient;
    private String primaryContact, residence, religion, nationalIdNumber;

    public static Person map(final PersonData personDTO) {
        Person person = new Person();
        person.setTitle(personDTO.getTitle());
        person.setGivenName(personDTO.getGivenName());
        person.setMiddleName(personDTO.getMiddleName());
        person.setSurname(personDTO.getSurname());
        person.setGender(personDTO.getGender());
        person.setPatient(personDTO.isPatient);
        person.setDateOfBirth(personDTO.getDateOfBirth());
        person.setDateRegistered(personDTO.getDateRegistered());
        if (personDTO.getAddress() != null) {           
            person.setAddresses(
                    personDTO.getAddress().stream()
                            .map(AddressDatas::map)
                            .collect(Collectors.toList())
            );
        }
        if (personDTO.getContact() != null) {
            person.setContacts(personDTO.getContact().stream().map(ContactData::map).collect(Collectors.toList()));
        }
        person.setMaritalStatus(personDTO.getMaritalStatus().name());

        person.setPrimaryContact(personDTO.getPrimaryContact());
        person.setResidence(personDTO.getResidence());
        person.setNationalIdNumber(personDTO.getNationalIdNumber());
        person.setReligion(personDTO.getReligion());
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
        persondto.setCreatedOn(LocalDate.from(person.getCreatedOn().atZone(ZoneId.systemDefault())));
        if (person.getAddresses() != null) {
            persondto.setAddressLine(person.getAddresses().get(0).getLine1());
            
            persondto.setAddress(
                    person.getAddresses().stream()
                            .map(AddressDatas::map)
                            .collect(Collectors.toList())
            );
        }
        if (person.getContacts() != null) {
            persondto.setPhone(person.getContacts().get(0).getTelephone());
            persondto.setContact(person.getContacts().stream().map(ContactData::map).collect(Collectors.toList()));
        }
        persondto.setMaritalStatus(MaritalStatus.valueOf(person.getMaritalStatus()));
        persondto.setPrimaryContact(person.getPrimaryContact());
        persondto.setResidence(person.getResidence());
        persondto.setNationalIdNumber(person.getNationalIdNumber());
        persondto.setReligion(person.getReligion());
        persondto.setDateRegistered(person.getDateRegistered());
        return persondto;
    }
}

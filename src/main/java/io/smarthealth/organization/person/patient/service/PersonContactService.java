/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.person.domain.Person;
import io.smarthealth.organization.person.domain.PersonContact;
import io.smarthealth.organization.person.domain.PersonContactRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.Waweru
 */
@Service
public class PersonContactService {

    @Autowired
    PersonContactRepository personContactRepository;

    public PersonContact createPersonContact(PersonContact personContact) {
        return personContactRepository.save(personContact);
    }

    public PersonContact fetchPersonPrimaryContact(Person person) {
        return personContactRepository.findByPersonAndIsPrimary(person, true).orElseThrow(() -> APIException.notFound("No primary contact found", ""));
    }

    public Page<PersonContact> fetchPersonsContact(Person person, Pageable pageable) {
        return personContactRepository.findByPerson(person, pageable);
    }

}

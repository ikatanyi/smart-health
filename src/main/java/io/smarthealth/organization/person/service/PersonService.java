/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.service;

import io.smarthealth.organization.person.domain.Person;
import io.smarthealth.organization.person.domain.PersonAddress;
import io.smarthealth.organization.person.domain.PersonAddressRepository;
import io.smarthealth.organization.person.domain.PersonContact;
import io.smarthealth.organization.person.domain.PersonContactRepository;
import io.smarthealth.organization.person.domain.PersonRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    PersonContactRepository personContactRepository;

    @Autowired
    PersonAddressRepository personAddressRepository;

    public PersonAddress fetchAddressById(Long addressId) {
        return personAddressRepository.getOne(addressId);
    }

    public PersonContact fetchContactById(Long contactId) {
        return personContactRepository.getOne(contactId);
    }

    public Person fetchPersonById(Long personId) {
        return personRepository.getOne(personId);
    }

    List<PersonContact> fetchContactsByPerson(final Person person) {
        return personContactRepository.findByPerson(person);
    }

    Page<PersonContact> fetchContactsByPerson(final Person person, final Pageable pageable) {
        return personContactRepository.findByPerson(person, pageable);
    }

    Page<PersonAddress> fetchAddressByPerson(final Person person, final Pageable pageable) {
        return personAddressRepository.findByPerson(person, pageable);
    }

    public boolean contactExists(Long contactId) {
        return personContactRepository.existsById(contactId);
    }

    public boolean addressExists(Long addresId) {
        return personAddressRepository.existsById(addresId);
    }

    @Transactional
    public PersonAddress updatePersonAddress(PersonAddress address) {
        return personAddressRepository.save(address);
    }

    @Transactional
    public PersonContact updatePersonContact(PersonContact contact) {
        return personContactRepository.save(contact);
    }

}

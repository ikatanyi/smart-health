/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.contact.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.contact.domain.Contact;
import io.smarthealth.organization.contact.domain.ContactRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
public class ContactService {

    @Autowired
    ContactRepository contactRepository;

    public Contact createContact(Contact contact) {
        try {
            return contactRepository.saveAndFlush(contact);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("An error occurred while creating contact ", e.getMessage());
        }
    }

    public List<Contact> createMultipleContacts(List<Contact> contacts) {
        try {
            return contactRepository.saveAll(contacts);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("An error occurred while creating contact ", e.getMessage());
        }
    }

    public Page<Contact> fetchAllContacts(final Pageable pgbl) {
        try {
            return contactRepository.findAll(pgbl);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("An error occurred while fetching all contacts ", e.getMessage());
        }
    }

}

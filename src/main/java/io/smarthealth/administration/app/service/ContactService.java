/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.app.service;

import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.administration.app.domain.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    public Contact createContact(Contact c) {
        return contactRepository.save(c);
    }
}

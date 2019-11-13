package io.smarthealth.administration.app.service;

import io.smarthealth.administration.app.data.AddressData;
import io.smarthealth.administration.app.data.ContactData;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.AddressRepository;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.administration.app.domain.ContactRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class AdminService {

    private final AddressRepository addressRepository;
    private final ContactRepository contactRepository;

    public AdminService(AddressRepository addressRepository, ContactRepository contactRepository) {
        this.addressRepository = addressRepository;
        this.contactRepository = contactRepository;
    }

    public List<Contact> createContacts(List<ContactData> contactList) {
        List<Contact> contacts = contactList
                .stream()
                .map(contc -> ContactData.map(contc))
                .collect(Collectors.toList());
        return contactRepository.saveAll(contacts);
    }
    
    public Contact createContact(ContactData contactData) {
        Contact contact = ContactData.map(contactData);
        return contactRepository.save(contact);
    }

    public Address createAddress(AddressData address) {
        Address addresses = AddressData.map(address);

        return addressRepository.save(addresses);
    }

    public List<Address> createAddresses(List<AddressData> addressList) {
        List<Address> addresses = addressList
                .stream()
                .map(adds -> AddressData.map(adds))
                .collect(Collectors.toList());

        return addressRepository.saveAll(addresses);
    }

    public Optional<Address> getAddress(Long id) {
        return addressRepository.findById(id);
    }

    public Optional<Contact> getContact(Long id) {
        return contactRepository.findById(id);
    }

    public Address getAddressWithNoFoundDetection(Long id) {
        return getAddress(id)
                .orElseThrow(() -> APIException.notFound("Address with id {0} not found", id));
    }

    public Contact getContactWithNoFoundDetection(Long id) {
        return getContact(id)
                .orElseThrow(() -> APIException.notFound("Contact with id {0} not found", id));
    }
}

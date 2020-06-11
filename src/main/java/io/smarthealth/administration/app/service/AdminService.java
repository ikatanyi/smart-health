package io.smarthealth.administration.app.service;

import io.smarthealth.administration.banks.domain.BankBranchRepository;
import io.smarthealth.administration.banks.domain.BankRepository;
import io.smarthealth.administration.banks.domain.BankBranch;
import io.smarthealth.administration.banks.domain.Bank;
import io.smarthealth.administration.app.data.AddressDat;
import io.smarthealth.administration.app.data.ContactData;
import io.smarthealth.administration.app.domain.*;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.org.domain.Organisation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Contact> createContactEntity(List<Contact> contactList) {
        return contactRepository.saveAll(contactList);
    }

    public Contact createContact(ContactData contactData) {
        Contact contact = ContactData.map(contactData);
        return contactRepository.save(contact);
    }

    public Address createAddress(AddressDat address) {
        Address addresses = AddressDat.map(address);

        return addressRepository.save(addresses);
    }

    public Address updateAddress(Long id, AddressDat addressData) {
        Address address = getAddressWithNoFoundDetection(id);
        if (addressData.getId() != null) {
            address.setId(addressData.getId());
        }
        if (addressData.getType() != null) {
            address.setType(Address.Type.valueOf(addressData.getType()));
        }
        address.setLine1(addressData.getLine1());
        address.setLine2(addressData.getLine2());
        address.setTown(addressData.getTown());
        address.setCounty(addressData.getCounty());
        address.setCountry(addressData.getCountry());
        address.setPostalCode(addressData.getPostalCode());

        return addressRepository.save(address);
    }

    public Contact updateContact(Long id, ContactData contactData) {
        Contact contacts = getContactWithNoFoundDetection(id);
        contacts.setId(contactData.getId());
        contacts.setSalutation(contactData.getSalutation());
        contacts.setFullName(contactData.getFullName());
        contacts.setEmail(contactData.getEmail());
        contacts.setMobile(contactData.getMobile());
        contacts.setTelephone(contactData.getTelephone());

        return contactRepository.save(contacts);
    }

    public List<Address> createAddresses(List<AddressDat> addressList) {
        List<Address> addresses = addressList
                .stream()
                .map(adds -> AddressDat.map(adds))
                .collect(Collectors.toList());

        return addressRepository.saveAll(addresses);
    }

    public List<Address> createAddressesEntity(List<Address> addressList) {
        return addressRepository.saveAll(addressList);
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

    public void removeAddressByOrganization(Organisation org) {
        if (!org.getAddress().isEmpty()) {
            addressRepository.delete(org.getAddress().get(0));
        }
    }

    public void removeContactByOrganization(Organisation org) {
        if (!org.getContact().isEmpty()) {
            contactRepository.delete(org.getContact().get(0));
        }
    }
}

package io.smarthealth.administration.app.service;

import io.smarthealth.administration.app.data.AddressData;
import io.smarthealth.administration.app.data.ContactData;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.AddressRepository;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.administration.app.domain.ContactRepository; 
import java.util.List;
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
    
    public List<Contact> createContacts(List<ContactData> contactList){
        List<Contact> contacts = contactList
                    .stream()
                    .map(contc -> ContactData.map(contc))
                    .collect(Collectors.toList());
        return contactRepository.saveAll(contacts);
    }
    
    public List<Address> createAddresses(List<AddressData> addressList){
         List<Address> addresses = addressList
                    .stream()
                    .map(adds -> AddressData.map(adds))
                    .collect(Collectors.toList());
         
         return addressRepository.saveAll(addresses);
    }
}

package io.smarthealth.administration.app.data;

import io.smarthealth.administration.app.domain.Contact;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ContactData {

    private Long id;
    private String salutation;
    private String firstName;
    private String lastName;
    private String contactRole;
    private String email;
    private String telephone;
    private String mobile;

    public static ContactData map(Contact contact) {
        ContactData data = new ContactData();
        data.setId(contact.getId());
        data.setSalutation(contact.getSalutation());
        data.setFirstName(contact.getFirstName());
        data.setLastName(contact.getLastName());
        data.setEmail(contact.getEmail());
        data.setMobile(contact.getMobile());
        data.setTelephone(contact.getTelephone());

        return data;
    }

    public static Contact map(ContactData contactData) {
        Contact contacts = new Contact();
        contacts.setId(contactData.getId());
        contacts.setSalutation(contactData.getSalutation());
        contacts.setFirstName(contactData.getFirstName());
        contacts.setLastName(contactData.getLastName());
        contacts.setEmail(contactData.getEmail());
        contacts.setMobile(contactData.getMobile());
        contacts.setTelephone(contactData.getTelephone());

        return contacts;
    }

}

package io.smarthealth.organization.domain;

import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.contact.domain.Address;
import io.smarthealth.organization.contact.domain.Contact;
import io.smarthealth.financial.account.domain.PaymentTerms;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.infrastructure.domain.Inheritable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Organization extends Inheritable {

    public enum Type {
        Company,
        Individual
    }

    private String code;
    private String name;
    private String taxId;
    private String website;
    private String country;
    private Boolean enabled;
     
    @OneToMany(mappedBy = "organization")
    private List<BankAccount> bankAccount = new ArrayList<>();

    @OneToOne
    private PaymentTerms creditLimit;
  
    @ManyToMany
    @JoinTable(name = "organization_address", joinColumns = {
        @JoinColumn(name = "organization_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "address_id", referencedColumnName = "id")})
    private List<Address> address = new ArrayList<>(); // this can be a shared addresses
  
    @ManyToMany
    @JoinTable(name = "organization_contacts", joinColumns = {
        @JoinColumn(name = "organization_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "contact_id", referencedColumnName = "id")})
    private List<Contact> contacts = new ArrayList<>();// like this to the parent entity

}

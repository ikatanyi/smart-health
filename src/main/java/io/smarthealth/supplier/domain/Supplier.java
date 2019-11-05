package io.smarthealth.supplier.domain;

import io.smarthealth.administration.app.domain.PaymentTerms;
import io.smarthealth.accounting.pricebook.domain.PriceBook;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.BankAccount;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.administration.app.domain.Currency;
import io.smarthealth.infrastructure.domain.Auditable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Supplier - Creditors - Vendor
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "supplier")
public class Supplier extends Auditable {

    public enum Type {
        Business,
        Individual
    }
    private Type supplierType;
    private String supplierName;
    private String legalName;
    private String taxNumber;
    private String website;
    private String notes;
    private boolean active;

    @Embedded
    private BankAccount bankAccount;

    @ManyToOne
    private PriceBook pricelist;
    
    @OneToOne
    private Currency currency;

    @OneToOne
    private PaymentTerms paymentTerms;

    @ManyToMany
    @JoinTable(name = "supplier_address", joinColumns = {
        @JoinColumn(name = "supplier_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "address_id", referencedColumnName = "id")})
    private List<Address> address = new ArrayList<>(); // this can be a shared addresses

    @ManyToMany
    @JoinTable(name = "supplier_contacts", joinColumns = {
        @JoinColumn(name = "supplier_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "contact_id", referencedColumnName = "id")})
    private List<Contact> contacts = new ArrayList<>();
}

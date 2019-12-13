package io.smarthealth.supplier.domain;

import io.smarthealth.accounting.acc.domain.AccountEntity;
import io.smarthealth.accounting.pricebook.domain.PriceBook;
import io.smarthealth.administration.app.data.AddressData;
import io.smarthealth.administration.app.data.BankAccountData;
import io.smarthealth.administration.app.data.ContactData;
import io.smarthealth.administration.app.domain.*;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.supplier.data.SupplierData;
import io.smarthealth.supplier.domain.enumeration.SupplierType;
import lombok.Data;

import javax.persistence.*;

/**
 * Supplier - Creditors - Vendor
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "supplier")
public class Supplier extends Auditable {

    @Enumerated(EnumType.STRING)
    private SupplierType supplierType;
    private String supplierName;
    private String legalName;
    private String taxNumber;
    private String website;
    private String notes;
    private boolean active;

    @Embedded
    private BankAccount bankAccount;

    @ManyToOne
    @JoinColumn(name = "pricelist_id", foreignKey = @ForeignKey(name = "fk_supplier_pricelist_id"))
    private PriceBook pricelist;

    @OneToOne
    @JoinColumn(name = "currency_id", foreignKey = @ForeignKey(name = "fk_supplier_currency_id"))
    private Currency currency;

    @OneToOne
    @JoinColumn(name = "payment_terms_id", foreignKey = @ForeignKey(name = "fk_supplier_payment_terms_id"))
    private PaymentTerms paymentTerms;

    @ManyToOne
    @JoinColumn
    private Address address;

    @OneToOne
    @JoinColumn
    private Contact contact;
    
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_supplier_account_id"))
    @ManyToOne
    private AccountEntity creditAccount;

//    @ManyToMany
//    @JoinTable(name = "supplier_address", joinColumns = {
//        @JoinColumn(name = "supplier_id", referencedColumnName = "id")}, inverseJoinColumns = {
//        @JoinColumn(name = "address_id", referencedColumnName = "id")})
//    private List<Address> address = new ArrayList<>(); // this can be a shared addresses
//
//    @ManyToMany
//    @JoinTable(name = "supplier_contacts", joinColumns = {
//        @JoinColumn(name = "supplier_id", referencedColumnName = "id")}, inverseJoinColumns = {
//        @JoinColumn(name = "contact_id", referencedColumnName = "id")})
//    private List<Contact> contacts = new ArrayList<>();

    

    public SupplierData toData() {
        SupplierData data = new SupplierData();
        data.setId(this.getId());
        data.setSupplierType(this.getSupplierType());
        data.setSupplierName(this.getSupplierName());
        data.setLegalName(this.getLegalName());
        data.setTaxNumber(this.getTaxNumber());
        data.setWebsite(this.getWebsite());

        if (this.getCurrency() != null) {
            data.setCurrencyId(this.getCurrency().getId());
            data.setCurrency(this.getCurrency().getName());
        }
        if (this.getPricelist() != null) {
            data.setPricebookId(this.getPricelist().getId());
            data.setPricebook(this.getPricelist().getName());
        }

        if (this.getPaymentTerms() != null) {
            data.setPaymentTermsId(this.getPaymentTerms().getId());
            data.setPaymentTerms(this.getPaymentTerms().getTermsName());
        }

        if (this.getBankAccount() != null) {
            data.setBank(BankAccountData.map(this.getBankAccount()));
        }
        if (this.getAddress() != null) {
            data.setAddresses(AddressData.map(this.getAddress()));
        }
        if (this.getContact() != null) {
            data.setContact(ContactData.map(this.getContact()));
        }
       if(this.getCreditAccount()!=null){
           data.setCreditAccountId(this.getCreditAccount().getId());
           data.setCreditAccount(this.getCreditAccount().getName());
       }
        data.setStatus(this.isActive() ? "Active" : "Inactive");
        data.setNotes(this.getNotes());

        return data;
    }
}

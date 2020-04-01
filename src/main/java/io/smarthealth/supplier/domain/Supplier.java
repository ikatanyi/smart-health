package io.smarthealth.supplier.domain;

import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.payment.domain.Payment;
import io.smarthealth.administration.finances.domain.PaymentTerms;
import io.smarthealth.accounting.pricelist.domain.PriceBook;
import io.smarthealth.administration.app.data.AddressData;
import io.smarthealth.administration.app.data.BankEmbeddedData;
import io.smarthealth.administration.app.data.ContactData;
import io.smarthealth.administration.app.domain.*;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.supplier.data.SupplierData;
import io.smarthealth.supplier.domain.enumeration.SupplierType;
import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Supplier - Creditors - Vendor
 *
 * @author Kelsas
 */
@Data
@Entity
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
    private BankEmbedded bankAccount;

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
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_supplier_address_id"))
    private Address address;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_supplier_contacts_id"))
    private Contact contact;

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_supplier_account_id"))
    @ManyToOne
    private Account creditAccount;
   
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
            data.setBank(BankEmbeddedData.map(this.getBankAccount()));
        }
        if (this.getAddress() != null) {
            data.setAddresses(AddressData.map(this.getAddress()));
        }
        if (this.getContact() != null) {
            data.setContact(ContactData.map(this.getContact()));
        }
        if (this.getCreditAccount() != null) {
            data.setCreditAccountId(this.getCreditAccount().getId());
            data.setCreditAccount(this.getCreditAccount().getName());
        }
        data.setStatus(this.isActive() ? "Active" : "Inactive");
        data.setNotes(this.getNotes());

        return data;
    }
}

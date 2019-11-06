package io.smarthealth.debtor.payer.domain;

import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.administration.app.domain.PaymentTerms;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.BankAccount;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.org.domain.Organization;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * {@link  Organization} Payer - Debtor
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "payers")
public class Payer extends Auditable {

    public enum Type {
        Business,
        Individual
    }
    private Type payerType;
    private String payerName;
    private String legalName;
    private String taxNumber;
    private String website;

    @Embedded
    private BankAccount bankAccount;

    @OneToOne
    private PaymentTerms paymentTerms;

    @Column(name = "is_insurance")
    private boolean insurance;
    
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payer_account_id"))
    @ManyToOne
    private Account debitAccount;

    @ManyToMany
    @JoinTable(name = "payer_address", joinColumns = {
        @JoinColumn(name = "payer_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "address_id", referencedColumnName = "id")})
    private List<Address> address = new ArrayList<>(); // this can be a shared addresses

    @ManyToMany
    @JoinTable(name = "payer_contacts", joinColumns = {
        @JoinColumn(name = "payer_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "contact_id", referencedColumnName = "id")})
    private List<Contact> contacts = new ArrayList<>();

}

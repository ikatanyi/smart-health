package io.smarthealth.debtor.payer.domain;

import io.smarthealth.accounting.acc.domain.AccountEntity;
import io.smarthealth.accounting.payment.domain.PaymentTerms;
import io.smarthealth.accounting.pricebook.domain.PriceBook;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.BankBranch;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.org.domain.Organization;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
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

    @JoinColumn(name = "bank_branch", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_payer_bank_branch_id")/*, insertable = false, updatable = false*/)
    @ManyToOne(optional = false)
    private BankBranch bankBranch;

    @OneToOne
    private PaymentTerms paymentTerms;

    @Column(name = "is_insurance")
    private boolean insurance;

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payer_account_id"))
    @ManyToOne
    private AccountEntity debitAccount;

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_price_book_id"))
    @ManyToOne
    private PriceBook priceBook;

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

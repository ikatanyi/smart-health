package io.smarthealth.debtor.payer.domain;

import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.payment.domain.Remittance;
import io.smarthealth.administration.finances.domain.PaymentTerms;
import io.smarthealth.accounting.pricelist.domain.PriceBook;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.banks.domain.BankBranch;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.debtor.claim.dispatch.domain.Dispatch;
import io.smarthealth.debtor.payer.domain.enumeration.Type;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.org.domain.Organisation;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

/**
 * {@link  Organisation} Payer - Debtor
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "payers")
public class Payer extends Auditable {

    

    @Enumerated(EnumType.STRING)
    private Type payerType;

    private String payerName;
    private String legalName;
    private String taxNumber;
    private String website;
    private String accountNumber;

    @OneToMany(mappedBy = "payer")
    private List<Remittance> remittances;

    @OneToMany(mappedBy = "payer")
    private List<Dispatch> dispatches;

    @JoinColumn(name = "bank_branch", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_payer_bank_branch_id")/*, insertable = false, updatable = false*/)
    @ManyToOne(optional = false)
    private BankBranch bankBranch;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payer_payment_terms_id"))
    private PaymentTerms paymentTerms;

    @Column(name = "is_insurance")
    private boolean insurance;

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payer_account_id"))
    @ManyToOne
    private Account debitAccount;

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

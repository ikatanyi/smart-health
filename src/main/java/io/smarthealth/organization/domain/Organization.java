package io.smarthealth.organization.domain;

import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.contact.domain.Address;
import io.smarthealth.organization.contact.domain.Contact;
import io.smarthealth.accounting.payment.domain.PaymentTerms;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Organization implements Serializable {

    public enum Type {
        Company,
        Individual
    }
    @Id
    @Column(length = 38, unique = true)
    private String id;
    private String code;
    private String name;
    private String taxId;
    private String website;
    private String country;
    private Boolean enabled;
    @CreatedDate
    protected Instant createdOn;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    protected Instant lastModifiedOn;
    @LastModifiedBy
    private String lastModifiedBy;

    @PrePersist
    public void autofill() {
        String ids = UUID.randomUUID().toString();
        this.setId(ids);
    }

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

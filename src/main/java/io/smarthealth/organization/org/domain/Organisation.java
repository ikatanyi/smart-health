package io.smarthealth.organization.org.domain;

import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.organization.facility.domain.Facility;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
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
@Table(name = "org_organization")
public class Organisation implements Serializable {

    public enum Type {
        Business,
        Individual
    }

    @Id
    @Column(length = 38, unique = true)
    private String id;
    private String code;
    private Type organizationType;
    private String organizationName;
    private String legalName;
    private String taxNumber;
    private String website; 
    private Boolean active;

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
    private Set<OrganizationBank> bankAccount;

    @ManyToMany
    @JoinTable(name = "org_address", joinColumns = {
        @JoinColumn(name = "organization_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "address_id", referencedColumnName = "id")})
    private List<Address> address = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "org_contact", joinColumns = {
        @JoinColumn(name = "organization_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "contact_id", referencedColumnName = "id")})
    private List<Contact> contact = new ArrayList<>();

    @OneToMany(mappedBy = "organization")
    private List<Facility> facilities;
}

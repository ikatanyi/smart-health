package io.smarthealth.organization.company.domain;

import io.smarthealth.administration.app.data.AddressData;
import io.smarthealth.administration.app.data.ContactData;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.organization.company.data.CompanyData;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company_profile")
public class Company implements Serializable {

    @Id
    @Column(length = 38, unique = true)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @Column(name = "organization_name")
    private String name;
    private String location;
    private String taxId;
    private String companyId; //represents facility id
    private String fiscalYear;
    @NotBlank
    private String defaultLanguage;
    private String timeZone;
    private String dateFormat;
    private String currency;
     private String contactName;
    private String contactEmail;
//       @OneToOne(mappedBy = "company", fetch = FetchType.LAZY)
//    private CompanyLogo logo;
       
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_company_address_id"))
    private Address address;

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

    public CompanyData toData() {
        CompanyData data = new CompanyData();
        data.setId(this.id);
//        if (this.logo != null) {
//            data.setLogoId(this.logo.getId());
//        }
        data.setName(this.name);
        data.setLocation(this.location);
        data.setTaxId(this.taxId);
        data.setCompanyId(this.companyId);
        data.setLanguage(this.defaultLanguage);
        data.setTimeZone(this.timeZone);
        data.setDateFormat(this.dateFormat);
        data.setContactName(this.contactName);
        data.setContactEmail(this.contactEmail);

        data.setCurrency(this.getCurrency());

        if (this.getAddress() != null) {
            data.setAddress(AddressData.map(this.getAddress()));
        }
        
        return data;
    }
}

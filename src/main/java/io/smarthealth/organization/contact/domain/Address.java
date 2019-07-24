package io.smarthealth.organization.contact.domain;

import io.smarthealth.organization.domain.Organization;
import io.smarthealth.organization.partner.domain.Partner;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "addresses")
public class Address extends Identifiable {
 
    @ManyToMany(mappedBy = "address")
    private List<Organization> organizations;
   public enum Type{
       Billing,
       Office,
       Personal,
       Postal,
       Current,
       Permanent,
       Other
   }
   private String title;
   @Enumerated(EnumType.STRING)
   private Type type;
    private String line1;
    private String line2;
    private String town;
    private String County;
    private String Country;
    private String postalCode;
    private String email;
    private String phone;
    private String fax;
    private Boolean billing;
}

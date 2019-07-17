package io.smarthealth.organization.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "organization")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Organization extends Auditable {

    private String companyName;
    private String legalName;
    private String organizationType; // Sole Trader|Partnership|Private Limited Company|Traded Company|Charity or association|Company| Others
    private String taxId;
    private String website;

    @OneToMany(mappedBy = "organization")
    private List<OrganizationAddress> addresses;
    
    @OneToMany(mappedBy = "organization")
    private List<OrganizationContact> contacts;

}

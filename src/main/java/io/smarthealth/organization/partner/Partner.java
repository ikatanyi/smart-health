package io.smarthealth.organization.partner;

import io.smarthealth.infrastructure.domain.BankDetails;
import io.smarthealth.organization.domain.Organization;
import java.math.BigDecimal;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

/**
 * {@link  Organization} business partners like Suppliers and Insurance
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "organization_partner")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Partner extends Organization {

    public enum Type {
        Supplier,
        Insurance
    }
    /**
     * Partner Account Reference number
     */
    private String reference;
    private String code;
    private String currency;
    private BigDecimal creditLimit;
    private String taxNumber;
    private String creditTerms; //payment terms number of days
    private String notes;
    @Enumerated(EnumType.STRING)
    private Type partnerType;
    @Embedded
    private BankDetails bankDetails;

}

package io.smarthealth.organization.domain;

import io.smarthealth.common.domain.BankDetails;
import java.math.BigDecimal;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "organization_partner")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Partner extends Organization {

    /**
     * Supplier Account Reference number
     */
    private String reference;
    private String curreny;
    private BigDecimal creditLimit;
    private String vatNumber;
    private String creditTerms; //payment terms number of days
    private String notes;
     
    @Embedded
    private BankDetails bankDetails;

}

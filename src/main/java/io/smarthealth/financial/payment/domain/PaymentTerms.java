package io.smarthealth.financial.payment.domain;

import io.smarthealth.organization.domain.Organization;
import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_payment_terms")
public class PaymentTerms extends Auditable {

    @OneToOne(mappedBy = "creditLimit")
    private Organization organization;

    private String termsName;
    private String description;
    private double invoicePortion;
    private String dueDateBasedOn;
    private Integer creditDays;

}

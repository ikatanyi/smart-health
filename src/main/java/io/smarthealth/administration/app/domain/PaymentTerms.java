package io.smarthealth.administration.app.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "payment_terms")
public class PaymentTerms extends Auditable {

    private String termsName;
    private String description;
    private Integer creditDays;
    private Boolean active;
}

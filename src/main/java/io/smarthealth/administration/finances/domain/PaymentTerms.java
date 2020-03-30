package io.smarthealth.administration.finances.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Entity;
import javax.persistence.Table; 
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity 
@Table(name = "ref_payment_terms")
public class PaymentTerms extends Auditable {

    private String termsName;
    private String description;
    private Integer creditDays;
    private Boolean active;
}

package io.smarthealth.accounting.payment.domain;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Kelsas
 */
@Embeddable
@Data
public class Discount implements Serializable {

    public enum Type {
        Percentage,
        Amount
    }
    @Enumerated(EnumType.STRING)
    private Type marginType;
    private BigDecimal value;
}

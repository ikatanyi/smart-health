 package io.smarthealth.infrastructure.domain;

import java.math.BigDecimal;
import javax.persistence.Embeddable;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Embeddable
@Data
public class Discount {
    public enum Type{
        Amount,
        Percentage
    }
    private BigDecimal amount;
}

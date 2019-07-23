package io.smarthealth.administration.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Embeddable
@Data
public class Margin implements Serializable {

    public enum Type {
        Percentage,
        Amount
    }
    @Enumerated(EnumType.STRING)
    private Type marginType;
    private BigDecimal value;
}

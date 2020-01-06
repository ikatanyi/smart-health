package io.smarthealth.debtor.payer.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDate;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "payer_scheme")
public class Scheme extends Auditable {

    public enum Type {
        Corporate,
        Individual
    }

    public enum PolicyCover {
        Outpatient,
        Inpatient,
        Both
    }
    @Column(nullable = false)
    @ManyToOne
    private Payer payer;
    private String schemeCode;
    private String schemeName;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private PolicyCover policyCover;

    private Boolean active;
    // any other scheme configuration parameters will
    // have a single configuration class to hold configurations values for this
}

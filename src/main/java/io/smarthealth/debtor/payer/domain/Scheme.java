package io.smarthealth.debtor.payer.domain;

import io.smarthealth.debtor.scheme.domain.enumeration.PolicyCover;
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

    public enum SchemeType {
        Corporate,
        Individual
    }

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_scheme_payer_id"))
    private Payer payer;

    @Column(nullable = false, unique = true)
    private String schemeName;

    @Column(nullable = false, unique = true)
    private String schemeCode;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private SchemeType type;

    private Boolean active;

    @Enumerated(EnumType.STRING)
    private PolicyCover cover;
    //private String category;

    @Column(nullable = false, unique = true)
    private String telNo;
    private String mobileNo;
    private String emailAddress;
    private String line1;
    private String line2;

    // any other scheme configuration parameters will
    // have a single configuration class to hold configurations values for this
}

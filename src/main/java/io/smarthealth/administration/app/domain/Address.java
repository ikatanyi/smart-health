package io.smarthealth.administration.app.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "addresses")
public class Address extends Identifiable {

    public enum Type {
        Billing,
        Office,
        Personal,
        Postal,
        Current,
        Permanent,
        Other
    }
    private String title;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String line1;
    private String line2;
    private String town;
    private String county;
    private String country;
    private String postalCode;
    private String email;
    private String phone;
    private String fax;
}

 package io.smarthealth.infrastructure.domain;

import javax.persistence.MappedSuperclass;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@MappedSuperclass
@Data
public abstract class Address extends Identifiable{

    private String line1;
    private String line2;
    private String town;
    private String County;
    private String postalCode;
    private String Country;
}

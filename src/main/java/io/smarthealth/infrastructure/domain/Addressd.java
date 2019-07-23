package io.smarthealth.infrastructure.domain;

import javax.persistence.MappedSuperclass;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@MappedSuperclass
@Data
public abstract class Addressd extends Identifiable {
    
    private String line1;
    private String line2;
    private String town;
    private String County;
    private String Country;
    private String postalCode;
}

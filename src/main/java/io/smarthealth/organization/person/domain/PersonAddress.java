package io.smarthealth.organization.person.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
public class PersonAddress extends Identifiable {

    @ManyToOne
    private Person person;
    private String line1;
    private String line2;
    private String town;
    private String County;
    private String Country;
    private String postalCode;
    private boolean isPrimary = false;

}

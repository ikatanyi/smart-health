package io.smarthealth.organization.person.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "person_contacts")
public class PersonContact extends Identifiable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Person person;
    private String email;
    private String telephone;
    private String mobile;
    private boolean isPrimary = false;
}

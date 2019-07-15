package io.smarthealth.person.domain;

import io.smarthealth.common.domain.Contact;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "person_contacts")
public class PersonContact extends Contact {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Person person;
    
}

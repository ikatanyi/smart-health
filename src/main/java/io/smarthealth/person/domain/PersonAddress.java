package io.smarthealth.person.domain;

import io.smarthealth.common.domain.Address;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data 
public class PersonAddress extends Address {
 
    @ManyToOne
    private Person person;  
}

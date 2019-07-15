package io.smarthealth.organization.domain;

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
public class OrganizationAddress extends Address{

    @ManyToOne
    private Organization organization;
     private String type; 
     private String name;
     
}

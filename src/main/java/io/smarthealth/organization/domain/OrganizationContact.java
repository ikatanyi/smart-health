package io.smarthealth.organization.domain;

import io.smarthealth.common.domain.Contact;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
public class OrganizationContact extends Contact {

    @ManyToOne
    private Organization organization;
    private String fullName; // who do we contact
    private String role; // the role 
}

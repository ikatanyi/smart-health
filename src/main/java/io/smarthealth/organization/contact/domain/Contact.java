package io.smarthealth.organization.contact.domain;

import io.smarthealth.organization.domain.Organization;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "contacts")
public class Contact extends Identifiable {

    @ManyToMany(mappedBy = "contacts")
    private List<Organization> organizations;
 
    private String fullName;
    private String contactRole;
    private String email;
    private String telephone;
    private String mobile;
}

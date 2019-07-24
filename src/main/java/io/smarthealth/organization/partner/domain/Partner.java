package io.smarthealth.organization.partner.domain;
 
import io.smarthealth.organization.domain.Organization;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) 
public class Partner extends Organization{ 
    
    
}

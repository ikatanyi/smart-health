package io.smarthealth.company.partner.domain;
 
import io.smarthealth.company.domain.Organization;
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

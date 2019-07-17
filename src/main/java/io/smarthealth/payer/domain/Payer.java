package io.smarthealth.payer.domain;

import io.smarthealth.organization.domain.Partner;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *  Debtors/Insurances
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "payer_insurance")
public class Payer extends Partner{
    
    @OneToMany(mappedBy = "payer")
    private Set<Scheme> schemes=new HashSet<>();
   
}

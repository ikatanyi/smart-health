package io.smarthealth.accounting.taxes.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.Table; 
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Kelsas
 */
@Data
@Entity 
@Table(name = "ref_taxes")
public class Tax extends Identifiable {

    private String taxName;
    private double rate;
    private Boolean active;
    
     @Override
    public String toString() {
        return "Tax [id=" + getId() + ", tax name=" + taxName + ", rate=" + rate + "]";
    }
}

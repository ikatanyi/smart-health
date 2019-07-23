package io.smarthealth.financial.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 * 
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_price_rule")
public class PriceRule extends Identifiable{

    public enum ApplyOn {
        ItemCode,
        ItemGroup
    }
  
    private String title;
    
}

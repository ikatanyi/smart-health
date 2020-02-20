package io.smarthealth.accounting.cashier.data;

import io.smarthealth.accounting.cashier.domain.*;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table; 
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data 
public class CashPointData{
    private Long id;
    
    private String name;
    
    private String[] tenderTypes;
 
    private boolean active;
}

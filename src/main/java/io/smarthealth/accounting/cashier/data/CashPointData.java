package io.smarthealth.accounting.cashier.data;

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

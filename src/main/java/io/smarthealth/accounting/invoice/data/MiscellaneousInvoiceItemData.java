/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.invoice.data;

import java.math.BigDecimal;
import lombok.Data;
import io.smarthealth.accounting.invoice.domain.MiscellaneousInvoiceItem;
/**
 *
 * @author Kelsas
 */
@Data
public class MiscellaneousInvoiceItemData {

    private Long id;
    private String description;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal amount;

    public static MiscellaneousInvoiceItemData map(MiscellaneousInvoiceItem inv){
        MiscellaneousInvoiceItemData data = new MiscellaneousInvoiceItemData();
        
        return data;
    }
}

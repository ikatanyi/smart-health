/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.invoice.data;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data 
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceReceipt {
    public enum Type{
        Copayment,
        Receipt
    }
    private Long id;
    private Type type;    
    private String reportType;
    private String reference;
    private BigDecimal amount;
    private LocalDate billingDate;
    private String itemName;
    private Double quantity;
    private BigDecimal price;

}

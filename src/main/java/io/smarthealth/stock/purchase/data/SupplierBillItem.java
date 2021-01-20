/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.purchase.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class SupplierBillItem {

    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private String invoiceNumber;
    private BigDecimal invoiceAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal netAmount;
    private String reference;
    private String reason;
    private String accountIdentifier;
    private String accountName;
    private String transactionId;
}

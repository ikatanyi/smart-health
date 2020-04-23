/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.accounts;

import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class InsuranceInvoiceData {
    private BigDecimal amount;
    private BigDecimal balance;
    private BigDecimal discount;
    private BigDecimal paid;
    private String patientId;
    private String patientName;
    private String memberName;
     private String memberNumber;
    private String payer;
    private String scheme;
    private String number;   
    private LocalDate dueDate;
    private LocalDate date;
    private InvoiceStatus status;
    private BigDecimal lab = BigDecimal.ZERO;
    private BigDecimal pharmacy = BigDecimal.ZERO;
    private BigDecimal radiology = BigDecimal.ZERO;
    private BigDecimal consultation = BigDecimal.ZERO;
    private BigDecimal procedure = BigDecimal.ZERO;
    private BigDecimal other = BigDecimal.ZERO;
}

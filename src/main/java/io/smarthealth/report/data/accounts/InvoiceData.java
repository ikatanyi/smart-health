/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.accounts;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class InvoiceData {
    private BigDecimal amount;
    private BigDecimal balance;
    private BigDecimal discount;
    private BigDecimal copay;
    private String patientId;
    private String patientName;
    private String payer;
    private String payee;
    private String invoiceNo;   
    private String terms;
    private String dueDate;
    private String createdBy;
    private String date;
    private List<InvoiceItemData>items;
}

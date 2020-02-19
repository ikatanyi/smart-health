/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.accounts;

import io.smarthealth.accounting.invoice.domain.InvoiceLineItem;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class InvoiceData {
    private Double amount;
    private Double balance;
    private Double discount;
    private Double copay;
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

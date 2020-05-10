/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.accounts;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class DailyBillingData {

    private Double amount;
    private Double balance;
    private Double paid;
    private String visitNo;
    private String patientId;
    private String patientName;
    private String receiptNo;   
    private String serviceType;
    private String paymentMode;
    private String createdBy;
    private LocalDate createdOn;
    private String lastModifiedBy;
    private Double lab;
    private Double pharmacy;
    private Double radiology;
    private Double consultation;
    private Double procedure;
    private Double other;
    private Boolean isWalkin;
}

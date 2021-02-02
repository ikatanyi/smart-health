/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.invoice.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class RebateInvoice {

    private Long payerId;
    private String payerName;
    private Long schemeId;
    private String schemeName;
    private String patientNumber;
    private String visitNumber;
    private String invoiceNumber;
    private String claimNumber;
    private BigDecimal amount;
    private String memberNumber;
    private String memberName;
    private String description;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate date;
}

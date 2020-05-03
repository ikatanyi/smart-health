/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.billing.data.nue;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bills {

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate date;
    private String patientNumber;
    private String patientName;
    private String paymentMode;
    private BigDecimal amount;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal netAmount;
    private String type; // Patient | Walkin
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.billing.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class PatientBalance {

    private String billNo;
    private String visitNo;
    private String patientNumber;
    private String patientName;
    private BigDecimal amountDeposited = BigDecimal.ZERO;
    private BigDecimal amountUsed = BigDecimal.ZERO;
    private BigDecimal amountRefunded = BigDecimal.ZERO;
    private BigDecimal depositBalance = BigDecimal.ZERO;
    private BigDecimal billAmount = BigDecimal.ZERO;
    private BigDecimal amountDue = BigDecimal.ZERO;

}

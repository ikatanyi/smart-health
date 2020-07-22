/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.payer.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayerStatement implements Serializable {

    private BigInteger payerId;
    private String payerName;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    private String transactionType;
    private String description;
    private String reference;
    private String invoiceNo;
    private Double originalAmount;
    private Double balanceAmount;
    private Double runningAmount;
}

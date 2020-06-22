/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.supplier.data;

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
public class SupplierStatement implements Serializable {
    private BigInteger supplierId;
    private String supplierName;
    @Temporal(TemporalType.DATE)
    private Date invoiceDate;
    private String description;
    private String narration;
    private String voucherNo;
    private String cheque;
    private String invoiceNo;
    private BigDecimal originalAmount;
    private BigDecimal balanceAmount;
    private BigDecimal runningAmount;
}

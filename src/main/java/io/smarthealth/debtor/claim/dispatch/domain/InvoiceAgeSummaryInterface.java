/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.claim.dispatch.domain;

import java.math.BigDecimal;

/**
 *
 * @author kent
 */
public interface InvoiceAgeSummaryInterface {
     public String getDescription();
     public BigDecimal getAmount();
     public String getPayerName();
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class InterbankData {
   private String fromAccount;
   private String toAccount;
   private BigDecimal amount;
   private String description; 
   private String transactionNo;
}

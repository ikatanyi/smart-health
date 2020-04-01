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
public class ReceiptMethod {
    private String currency;
    private String method;
    private BigDecimal amount;
    private String reference;
    private String type;
    //what if this was paid from bank I should be able to t
    private String accountNumber;
}

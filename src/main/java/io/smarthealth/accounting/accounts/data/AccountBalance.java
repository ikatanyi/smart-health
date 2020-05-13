/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.accounts.data;

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
public class AccountBalance {

    private String accountNumber;
    private String accountName;
    private String description;
    private BigDecimal balance; 
}

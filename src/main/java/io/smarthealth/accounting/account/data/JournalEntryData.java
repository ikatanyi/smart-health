/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.data;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@AllArgsConstructor
public class JournalEntryData {

    private String accountNumber;
    private Double credit;
    private Double debit;
    private String comments;
}

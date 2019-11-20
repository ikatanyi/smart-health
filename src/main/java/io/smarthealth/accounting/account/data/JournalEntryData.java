/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.data;

import io.smarthealth.accounting.account.domain.JournalEntry;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@AllArgsConstructor
public class JournalEntryData {

    private Long journalEntryId;
    private LocalDate entryDate;
    private String journalId;
    private String accountNumber;
    private String accountName;
    private Double credit;
    private Double debit;
    private String description;
    private BigDecimal runningBalance;
    private boolean balanceCalculted;

    public static JournalEntryData map(JournalEntry entity) {
        return new JournalEntryData(
                entity.getId(),
                entity.getEntryDate(),
                entity.getJournal().getTransactionId(),
                entity.getAccount().getAccountNumber(),
                entity.getAccount().getAccountName(),
                entity.getCredit(),
                entity.getDebit(),
                entity.getDescription(),
                entity.getRunningBalance(),
                entity.isBalanceCalculated());
    }
    public boolean isDebit(){
        return debit !=0;
    }
}

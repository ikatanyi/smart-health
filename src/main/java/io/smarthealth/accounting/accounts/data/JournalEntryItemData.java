package io.smarthealth.accounting.accounts.data;

import io.smarthealth.accounting.accounts.domain.*;
import java.math.BigDecimal;
import java.time.LocalDate; 
import lombok.Data;

/**
 *
 * @author Kelsas
 */ 
@Data
public class JournalEntryItemData {

    private Long id;
    private Long journalId;
    private JournalState status;
    private TransactionType type;
    private String transactionNo;
    private LocalDate date;
    private String accountNumber;
    private String accountName; 
    private String description;
    private BigDecimal debit;
    private String formattedDebit;
    private BigDecimal credit;
    private String formattedCredit;
     private BigDecimal amount;
    private String formattedAmount;
     private String createdBy;

}

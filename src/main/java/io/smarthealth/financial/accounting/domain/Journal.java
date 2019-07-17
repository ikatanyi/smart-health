package io.smarthealth.financial.accounting.domain;

import io.smarthealth.common.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Map the accounts to the respective journals for eazy posting
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_journal")
public class Journal extends Identifiable {

    public enum Type {
        Stock,
        Sales,
        Purchase,
        Sales_Refund,
        Purchase_Refund,
        Miscellaneous,
        Opening_Entries,
        Cash,
        Bank
    }
    @Column(length = 64)
    private String name;
    //Accounts payable | cash book to their default accounts
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    private Account defaultCreditAccount;
    @ManyToOne
    private Account defaultDebitAccount;
 
}

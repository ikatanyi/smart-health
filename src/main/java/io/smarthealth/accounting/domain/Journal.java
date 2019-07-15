package io.smarthealth.accounting.domain;

import io.smarthealth.common.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_journal")
public class Journal extends Identifiable {

    public enum Type {
        Sale,
        Purchase,
        Bank,
        Cash,
        General
    }
    @Column(length = 64)
    private String name;
    //Accounts payable | cash book to their default accounts

    @ManyToOne
    private Account defaultCreditAccount;
    @ManyToOne
    private Account defaultDebitAccount;

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.acc.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 *
 * @author Kelsas
 */
@Data 
@Entity
@Table(name = "acc_account_entries")
public class AccountEntryEntity extends Identifiable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", foreignKey = @ForeignKey(name = "fk_account_entries_account_id"))
    private AccountEntity account;
    @Column(name = "a_type")
    private String type;
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    @Column(name = "message")
    private String message;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "balance")
    private Double balance;
}

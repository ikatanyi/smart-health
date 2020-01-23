package io.smarthealth.organization.bank.domain;

import io.smarthealth.accounting.acc.domain.AccountEntity;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.bank.domain.enumeration.BankType;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "bank_account")
public class BankAccount extends Auditable {

    @Enumerated(EnumType.STRING)
    private BankType bankType;
    
    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_bank_account_ledger_account_id"))
    private AccountEntity ledgerAccount;

//    @Column(nullable = false, unique = true)
    private String bankName;
    private String bankBranch;
    
    private String accountName;
    @Column(nullable = false, unique = true)
    private String accountNumber;
    private String currency;
    
    private String description;
    
    private Boolean isDefault;
}

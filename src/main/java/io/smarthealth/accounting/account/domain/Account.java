package io.smarthealth.accounting.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_account")
public class Account extends Identifiable {

    private String accountNumber;
    private String accountName;
    private String description;
    
    @OneToOne
    @JoinColumn(name = "account_type", foreignKey = @ForeignKey(name = "fk_account_account_type_id"))
    private AccountType accountType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_account_id", foreignKey = @ForeignKey(name = "fk_account_parent_account_id"))
    private Account parentAccount;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_account_id")
    private List<Account> children = new LinkedList<>();

    private Boolean showAccountsInChart = true;
    @Transient
    private BigDecimal balance;
    
    private Boolean enabled = true;

}

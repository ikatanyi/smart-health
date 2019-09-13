package io.smarthealth.accounting.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
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
    @OneToOne
    @JoinColumn(name = "account_type")
    private AccountType accountType;
    private String description;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_account_id")
    private Account parentAccount;
    private Boolean showAccountsInChart = true;
    private Boolean enabled = true;
    @Transient
    private BigDecimal balance;
}

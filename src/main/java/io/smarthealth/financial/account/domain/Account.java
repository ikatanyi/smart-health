package io.smarthealth.financial.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_account")
public class Account extends Identifiable {
    //this is the account number
    private String identifier;
    
    @Column(name = "account_name")
    private String name;
    
    @Column(name = "account_type")
    private String type;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_account_id")
    private Account referenceAccount;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ledger_id")
    private Ledger ledger;
    private boolean disabled = false;
    private Double balance;
}

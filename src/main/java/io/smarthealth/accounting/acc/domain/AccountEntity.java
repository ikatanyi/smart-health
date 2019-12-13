package io.smarthealth.accounting.acc.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import lombok.Data;

import javax.persistence.*;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "acc_accounts")
public class AccountEntity extends Auditable {

    @Column(name = "a_type")
    private String type;
    @Column(name = "identifier")
    private String identifier;
    @Column(name = "a_name")
    private String name;
//    @Column(name = "holders")
//    private String holders;
//    @Column(name = "signature_authorities")
//    private String signatureAuthorities;
    @Column(name = "balance")
    private Double balance;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_account_id", foreignKey = @ForeignKey(name = "fk_account_reference_account_id"))
    private AccountEntity referenceAccount;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) //ManyToOne
    @JoinColumn(name = "ledger_id",foreignKey = @ForeignKey(name = "fk_account_ledger_id"))
    private LedgerEntity ledger;
    @Column(name = "a_state")
    private String state;
    @Column(name = "alternative_account_number", length = 256, nullable = true)
    private String alternativeAccountNumber;
}

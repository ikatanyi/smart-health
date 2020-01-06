package io.smarthealth.organization.org.domain;

import io.smarthealth.accounting.acc.domain.AccountEntity;
import io.smarthealth.administration.app.domain.BankAccount;
import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "org_organization_bank")
public class OrganizationBank extends Auditable {

    @ManyToOne
    private Organization organization;

    @Embedded
    private BankAccount bank;
 
    @JoinColumn(name = "ledger_account_id", foreignKey = @ForeignKey(name = "fk_bank_account_id"))
    @ManyToOne
    private AccountEntity ledgerAccount;
    private Boolean defaultAccount;
}

package io.smarthealth.organization.payer.domain;

import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.organization.domain.Organization;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * {@link  Organization} Payer - Debtor
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "payer")
public class Payer extends Organization {

    @Enumerated(EnumType.STRING)
    private Type payerType;
    private boolean insurance;
    @ManyToOne
    private Account debitAccount;

}

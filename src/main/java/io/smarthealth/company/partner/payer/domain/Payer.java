package io.smarthealth.company.partner.payer.domain;

import io.smarthealth.company.partner.domain.Partner;
import io.smarthealth.financial.account.domain.Account;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "payer")
public class Payer extends Partner {

    @Enumerated(EnumType.STRING)
    private Type payerType;
    private boolean insurance;
    @ManyToOne
    private Account debitAccount;

}

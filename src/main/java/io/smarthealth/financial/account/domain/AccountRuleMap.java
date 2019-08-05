package io.smarthealth.financial.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
public class AccountRuleMap extends Identifiable {

    private String description; //define the 
    @ManyToOne
    private Account account;
}

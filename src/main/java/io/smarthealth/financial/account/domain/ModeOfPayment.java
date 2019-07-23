package io.smarthealth.financial.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_mode_of_payment")
public class ModeOfPayment extends Identifiable{
    public enum Type{
        Cash,
        Bank,
        General
    }
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
}

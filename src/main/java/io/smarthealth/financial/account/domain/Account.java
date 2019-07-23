package io.smarthealth.financial.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
public class Account extends Identifiable {

    public enum Type {
        Asset, Liability, Equity, Income, Expense
    }
    private String code;
    @Column(length = 64)
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
    @ManyToOne
    private Account parent;
}

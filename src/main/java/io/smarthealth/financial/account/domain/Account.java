package io.smarthealth.financial.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name="account_account", 
            uniqueConstraints = {
                @UniqueConstraint(name = "uk_account_uuid", columnNames= { "uuid" } )
            } )
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
    @JoinColumn(name = "account_parent", foreignKey = @ForeignKey(name = "fk_account_parent"))
    private Account parent;
}

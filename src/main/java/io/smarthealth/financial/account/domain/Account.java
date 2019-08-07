package io.smarthealth.financial.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_account")
public class Account extends Identifiable {

    public enum State {
        OPEN,
        LOCKED,
        CLOSED
    }
 
    @NaturalId
    @NotBlank(message = "A Unique Account Number is Required")
    @Column(nullable = false, updatable = false, unique = true, length = 30)
    private String accountCode;

    @Column(length = 64)
    private String accountName;

    @Enumerated(EnumType.STRING)
    private AccountType accountType; //defines the root account

    @JoinColumn(name = "account_parent", foreignKey = @ForeignKey(name = "fk_account_parent"))
    @ManyToOne
    private Account parent;

    private Boolean nonTransacting;
    
    private Boolean active;

    @OneToMany(mappedBy = "parent")
    private List<Account> children;

    @OneToOne(mappedBy = "account")
    private AccountBalance accountBalance;
    
}

package io.smarthealth.organization.facility.domain;

import io.smarthealth.accounting.acc.domain.AccountEntity;
import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.*;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility_department")
public class Department extends Identifiable {

    public enum Type {
        Patient,
        Store,
        ServicePoint
    }

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_dept_facility_id"))
    private Facility facility;

    @Enumerated(EnumType.STRING)
    private Type type;
    @Column(unique = true)
    private String code;
    @Column(name = "dept_name")
    private String name;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_dept_parent_id"))
    private Department parent;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_dept_income_account_id"))
    private AccountEntity incomeAccount;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_dept_expense_account_id"))
    private AccountEntity expenseAccount;

    private Boolean isStore; // the department can be store location

    private Boolean active;
    private String servicePointType;

}

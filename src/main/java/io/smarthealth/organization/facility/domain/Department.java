package io.smarthealth.organization.facility.domain;

import io.smarthealth.accounting.acc.domain.AccountEntity;
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
@Table(name = "facility_department",uniqueConstraints = {
    @UniqueConstraint(columnNames = {"dept_name"}, name="unique_dept_name_fk_dept_facility_id")})
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

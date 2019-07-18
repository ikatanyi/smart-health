package io.smarthealth.organization.facility.domain;

import io.smarthealth.financial.accounting.domain.Account;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

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
    private Facility facility;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String code;
    @Column(name = "dept_name")
    private String name;
    @ManyToOne
    private Department parent;
    @OneToOne
    private Account incomeAccount;
    @OneToOne
    private Account expenseAccount;

    private Boolean isStore; // the department can be store location

    private Boolean active;

}

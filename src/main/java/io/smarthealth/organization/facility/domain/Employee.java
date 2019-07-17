package io.smarthealth.organization.facility.domain;

import io.smarthealth.organization.facility.domain.ServiceLocation;
import io.smarthealth.organization.facility.domain.DepartmentUnit;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.auth.domain.User;
import io.smarthealth.organization.person.domain.Person;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility_employee")
public class Employee extends Person {

    public enum Category {
        Doctor,
        Nurse,
        Radiographer,
        Lab_Technologist,
        Pharmacist
    }
    @Enumerated(EnumType.STRING)
    private Category employeeCategory;
    private Department department;
    private DepartmentUnit departmentUnit;
    private ServiceLocation workLocation;

    @OneToOne
    @JoinColumn(name = "login_account")
    private User loginAccount;

    @Column(length = 50)
    private String status;
}

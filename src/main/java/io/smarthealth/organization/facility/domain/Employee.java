package io.smarthealth.organization.facility.domain;

import io.smarthealth.administration.employeespecialization.data.enums.EmployeeCategory;
import io.smarthealth.organization.person.domain.Person;
import io.smarthealth.security.domain.User;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility_employee") 
public class Employee extends Person {

    @Enumerated(EnumType.STRING)
    private EmployeeCategory.Category employeeCategory;
    @ManyToOne
    private Department department;

    @OneToOne
    @JoinColumn(name = "login_account")
    private User loginAccount;

    @Column(length = 50)
    private String status;

    @Column(length = 25, unique = true)
    private String staffNumber;

    private String specialization;
    private String licenseNo;

}

package io.smarthealth.organization.facility.domain;

import io.smarthealth.auth.domain.User;
import io.smarthealth.organization.person.domain.Person;
import lombok.Data;

import javax.persistence.*;

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
    @ManyToOne
    private Department department;

    @OneToOne
    @JoinColumn(name = "login_account")
    private User loginAccount;

    @Column(length = 50)
    private String status;

    @Column(length = 25, unique = true)
    private String staffNumber;
    
   
}

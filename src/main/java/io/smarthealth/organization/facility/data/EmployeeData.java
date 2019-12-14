/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.data;

import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.data.PersonData;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class EmployeeData extends PersonData {

    @Enumerated(EnumType.STRING)
    private Employee.Category employeeCategory;
    private String departmentName;
    private DepartmentData department;
    private DepartmentUnitData departmentUnit;
    private String departmentCode;
    private String status;
    private String staffNumber;

    private String email;
    private String mobile;
    private String telephone;

}

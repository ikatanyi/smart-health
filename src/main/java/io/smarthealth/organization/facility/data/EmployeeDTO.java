/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.data;

import io.smarthealth.organization.facility.domain.Employee;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class EmployeeDTO {

    @Enumerated(EnumType.STRING)
    private Employee.Category employeeCategory;

    private String departmentName;
    private DepartmentDTO department;
    private DepartmentUnitDTO departmentUnit;

}

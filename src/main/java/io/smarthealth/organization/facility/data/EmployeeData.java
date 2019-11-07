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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Simon.waweru
 */
@Data
public class EmployeeData extends PersonData {

    @Autowired
    ModelMapper modelMapper;

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
//    
//    public EmployeeData convertEmployeeEntityToEmployeeData(Employee employee) {
//        EmployeeData employeeData = modelMapper.map(employee, EmployeeData.class);
//        employeeData.setDepartmentCode(employee.getDepartment().getCode());
//        return employeeData;
//    }
}

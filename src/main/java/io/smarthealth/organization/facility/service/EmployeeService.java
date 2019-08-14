/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.data.EmployeeData;
import io.smarthealth.organization.facility.domain.DepartmentRepository;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.domain.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 *
 * @author Simon.waweru
 */
@Service
public class EmployeeService {

    /*
    a. Create a new  employee
    b. Read all  employees 
    c. Read employee by Id
    c. Update employee
    d. Fetch employee by number
    e. Fetch employee by category
     */
    @Autowired
    EmployeeRepository employeeRepository;
    
    @Autowired
    DepartmentRepository departmentRepository;
    
    @Transactional
    public Employee createFacilityEmployee(Employee employee) {
        //verify if exists
        if (employeeRepository.existsByStaffNumber(employee.getStaffNumber())) {
            throw APIException.conflict("Staff identified by number {0} already exists ", employee.getStaffNumber());
        }
        return employeeRepository.save(employee);
    }
    
    Page<Employee> fetchAllEmployees(final Pageable pg) {
        return employeeRepository.findAll(pg);
    }
    
    Page<Employee> fetchEmployeeByCategory(final String categoryName, final Pageable pg) {
        return employeeRepository.findByCategory(categoryName, pg);
    }
    
    public Employee fetchEmployeeByNumberOrThrow(final String staffNumber) {
        return employeeRepository.findByStaffNumber(staffNumber).orElseThrow(() -> APIException.notFound("Employee identified by number {0} was not found ", staffNumber));
    }
    
}

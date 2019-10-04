/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.api;

import io.smarthealth.infrastructure.common.APIResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.organization.facility.data.EmployeeData;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.DepartmentService;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.domain.PersonContact;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Simon.waweru
 */
@RestController
@RequestMapping("/api")
public class EmployeeController {

    @Autowired
    DepartmentService departmentService;

    @Autowired
    EmployeeService employeeService;

    @PostMapping("/employee")
    public @ResponseBody
    ResponseEntity<?> createEmployee(@RequestBody @Valid final EmployeeData employeeData) {
        Department department = departmentService.fetchDepartmentByCode(employeeData.getDepartmentCode());

        Employee employee = employeeService.convertEmployeeDataToEntity(employeeData);
        employee.setDepartment(department);
        employee.setEmployeeCategory(employeeData.getEmployeeCategory());

        PersonContact personContact = new PersonContact();
        personContact.setEmail(employeeData.getEmail());
        personContact.setMobile(employeeData.getMobile());
        personContact.setTelephone(employeeData.getTelephone());
        personContact.setPrimary(true);

        Employee savedEmployee = employeeService.createFacilityEmployee(employee, personContact);

        EmployeeData employeeData1 = employeeService.convertEmployeeEntityToEmployeeData(savedEmployee);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/facility/" + employeeData1.getDepartment().getFacilityCode() + "/department/{code}")
                .buildAndExpand(employeeData1.getDepartmentCode()).toUri();

        return ResponseEntity.created(location).body(APIResponse.successMessage("Employee was successfully created", HttpStatus.CREATED, employeeData1));
    }

    @GetMapping("/employee")
    public ResponseEntity<List<EmployeeData>> fetchAllEmployees(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Page<EmployeeData> page = employeeService.fetchAllEmployees(queryParams, pageable).map(p -> employeeService.convertEmployeeEntityToEmployeeData(p));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}

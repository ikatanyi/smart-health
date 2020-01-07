/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.api;

import io.smarthealth.infrastructure.common.ApiResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.organization.facility.data.EmployeeData;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.DepartmentService;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.domain.PersonContact;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Simon.waweru
 */
@Api
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

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/facility/" + employeeData1.getDepartment().getFacilityId() + "/department/{code}")
                .buildAndExpand(employeeData1.getDepartmentCode()).toUri();

        return ResponseEntity.created(location).body(ApiResponse.successMessage("Employee was successfully created", HttpStatus.CREATED, employeeData1));
    }

    @GetMapping("/employee")
    public ResponseEntity<List<EmployeeData>> fetchAllEmployees(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Page<EmployeeData> page = employeeService.fetchAllEmployees(queryParams, pageable).map(p -> employeeService.convertEmployeeEntityToEmployeeData(p));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/department/{code}/employee")
    public ResponseEntity<List<EmployeeData>> fetchEmployeesByDepartment(@PathVariable("code") final String departmentCode, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Department department = departmentService.fetchDepartmentByCode(departmentCode);
        List<Employee> employeeList = employeeService.findEmployeeByDepartment(queryParams, department, pageable);
        List<EmployeeData> employeeDataList = new ArrayList<>();

        for (Employee employee : employeeList) {
            employeeDataList.add(employeeService.convertEmployeeEntityToEmployeeData(employee));
        }

        return new ResponseEntity<>(employeeDataList, HttpStatus.OK);
    }

    @GetMapping("/employee/{category}")
    public ResponseEntity<List<EmployeeData>> fetchEmployeesByCategory(@PathVariable("category") final String category, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

        List<Employee> employeeList = employeeService.findEmployeeByCategory(queryParams, category, pageable);
        List<EmployeeData> employeeDataList = new ArrayList<>();

        for (Employee employee : employeeList) {
            employeeDataList.add(employeeService.convertEmployeeEntityToEmployeeData(employee));
        }

        return new ResponseEntity<>(employeeDataList, HttpStatus.OK);
    }
}

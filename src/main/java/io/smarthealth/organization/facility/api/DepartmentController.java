/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.api;

import io.smarthealth.organization.facility.data.DepartmentData;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.service.DepartmentService;
import io.swagger.annotations.Api;
import java.net.URI;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author Simon.waweru
 */
@RestController
@RequestMapping("/api")
@Api(value = "Department Controller", description = "Operations pertaining to department maintenance")
public class DepartmentController {

    @Autowired
    DepartmentService departmentService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/employees")
    public @ResponseBody
    ResponseEntity<?> createFacilityEmployee(@RequestBody @Valid final DepartmentData departmentData) {
        Department department = convertDeptDataToDepartment(departmentData);
        Department departmentSaved = this.departmentService.createDepartment(department);

        DepartmentData savedDeptData = convertToDeptData(departmentSaved);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/employees/{deptId}")
                .buildAndExpand(departmentSaved.getId()).toUri();

        return ResponseEntity.created(location).body(savedDeptData);
    }

    private Department convertDeptDataToDepartment(DepartmentData departmentData) {
        Department dept = modelMapper.map(departmentData, Department.class);
        return dept;
    }

    private DepartmentData convertToDeptData(Department department) {
        DepartmentData deptData = modelMapper.map(department, DepartmentData.class);
        return deptData;
    }
}

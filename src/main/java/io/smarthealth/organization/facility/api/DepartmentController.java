/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.organization.facility.data.DepartmentData;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.service.DepartmentService;
import io.smarthealth.organization.facility.service.FacilityService;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@Api(value = "Department Controller", description = "Operations pertaining to department maintenance")
public class DepartmentController {

    @Autowired
    DepartmentService departmentService;

    @Autowired
    FacilityService facilityService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/department")
    public @ResponseBody
    ResponseEntity<?> createFacilityDepartment(@RequestBody @Valid final DepartmentData departmentData) {
        Facility facility = facilityService.findFacility(departmentData.getFacilityId());

        Department department = convertDeptDataToDepartment(departmentData);
        department.setFacility(facility);
        Department departmentSaved = this.departmentService.createDepartment(department);

        DepartmentData savedDeptData = convertToDeptData(departmentSaved);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/facility/" + departmentData.getFacilityCode() + "/department/{code}")
                .buildAndExpand(departmentSaved.getCode()).toUri();

        return ResponseEntity.created(location).body(savedDeptData);
    }

    @GetMapping("/department")
    public ResponseEntity<List<DepartmentData>> fetchAllDepartments(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

        Page<DepartmentData> page = departmentService.fetchAllDepartments(pageable).map(d -> convertToDeptData(d));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/facility/{code}/department")
    public ResponseEntity<List<DepartmentData>> fetchDepartmentsByFacility(@PathVariable("code") final String facilityCode, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Facility facility = facilityService.findFacility(Long.valueOf(facilityCode));
        Page<DepartmentData> page = departmentService.fetchDepartmentByFacility(facility, pageable).map(d -> convertToDeptData(d));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
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

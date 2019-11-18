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
import org.modelmapper.convention.MatchingStrategies;
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
import org.springframework.web.bind.annotation.PutMapping;
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
        if (departmentData.getParentId() != null) {
            department.setParent(departmentService.fetchDepartmentById(departmentData.getParentId()));
        }
        department.setType(Department.Type.valueOf(departmentData.getType().name()));
        Department departmentSaved = this.departmentService.createDepartment(department);

        DepartmentData savedDeptData = convertToDeptData(departmentSaved);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/facility/" + departmentData.getFacilityId() + "/department/{code}")
                .buildAndExpand(departmentSaved.getCode()).toUri();
        return new ResponseEntity<>(savedDeptData, HttpStatus.CREATED);
    }

    @PutMapping("/department/{id}")
    public @ResponseBody
    ResponseEntity<?> updateFacilityDepartment(@PathVariable("id") final Long id, @RequestBody @Valid final DepartmentData departmentData) {
        Department department = departmentService.fetchDepartmentById(id);
        Facility facility = facilityService.findFacility(departmentData.getFacilityId());
        department.setFacility(facility);
        department.setCode(departmentData.getCode());
        department.setActive(departmentData.isActive());
        department.setFacility(facility);
        department.setIsStore(departmentData.isStore());
        department.setName(departmentData.getName());
        if (departmentData.getParentId() != null) {
            department.setParent(departmentService.fetchDepartmentById(departmentData.getParentId()));
        }
        department.setServicePointType(departmentData.getServicePointType().name());
        department.setType(Department.Type.valueOf(departmentData.getType().name()));
        Department departmentSaved = this.departmentService.createDepartment(department);

        DepartmentData savedDeptData = convertToDeptData(departmentSaved);

        return new ResponseEntity<>(savedDeptData, HttpStatus.CREATED);
    }

    @GetMapping("/department")
    public ResponseEntity<List<DepartmentData>> fetchAllDepartments(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

        Page<DepartmentData> page = departmentService.fetchAllDepartments(pageable).map(d -> convertToDeptData(d));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/facility/{id}/department")
    public ResponseEntity<List<DepartmentData>> fetchDepartmentsByFacility(@PathVariable("id") final String facilityId, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Facility facility = facilityService.findFacility(Long.valueOf(facilityId));
        Page<DepartmentData> page = departmentService.fetchDepartmentByFacility(facility, pageable).map(d -> convertToDeptData(d));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/logged-in-facility/department")
    public ResponseEntity<List<DepartmentData>> fetchDepartmentsByLoggedFacility(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Facility facility = facilityService.loggedFacility();
        Page<DepartmentData> page = departmentService.fetchDepartmentByFacility(facility, pageable).map(d -> convertToDeptData(d));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private Department convertDeptDataToDepartment(DepartmentData departmentData) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Department dept = modelMapper.map(departmentData, Department.class);
//Department dept = DepartmentData.
        //if (EnumExists.isInEnum("Open", Department.ServicePointType.class)) {
        dept.setServicePointType(departmentData.getServicePointType().name());
        //}
        return dept;
    }

    private DepartmentData convertToDeptData(Department department) {
        DepartmentData d = modelMapper.map(department, DepartmentData.class);
        d.setFacilityId(department.getFacility().getId());
        d.setFacilityName(department.getFacility().getFacilityName());
        if (department.getParent() != null) {
            d.setParentId(department.getParent().getId());
            d.setParentName(department.getParent().getName());
        }
        d.setType(department.getType());
        return d;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.employeespecialization.api;

import io.smarthealth.administration.employeespecialization.data.EmployeeSpecializationData;
import io.smarthealth.administration.employeespecialization.data.enums.EmployeeCategory;
import io.smarthealth.administration.employeespecialization.domain.EmployeeSpecialization;
import io.smarthealth.administration.employeespecialization.service.EmployeeSpecializationService;
import io.smarthealth.infrastructure.common.ApiResponse;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Simon.waweru
 */
@Api
@RestController
@RequestMapping("/api")
public class EmployeeSpecializationController {

    @Autowired
    EmployeeSpecializationService employeeSpecializationService;

    @PostMapping("/employee-specialization")
    @PreAuthorize("hasAuthority('create_employeeSpecialization')")
    public ResponseEntity<?> createEmployeeSpecialization(@Valid @RequestBody EmployeeSpecializationData employeeSpecializationData) {
        EmployeeSpecialization result = employeeSpecializationService.createEmployeeSpecialization(EmployeeSpecializationData.map(employeeSpecializationData));
        Pager<EmployeeSpecializationData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Specialization details created successfully");
        pagers.setContent(EmployeeSpecializationData.map(result));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/employee-specialization")
    @PreAuthorize("hasAuthority('view_employeeSpecialization')")
    public ResponseEntity<?> fetchAllEmployeeSpecializations() {

        List<EmployeeSpecialization> specialization = employeeSpecializationService.fetchAllSpecializations();
        List<EmployeeSpecializationData> data = new ArrayList<>();
        for (EmployeeSpecialization sp : specialization) {
            EmployeeSpecializationData d = EmployeeSpecializationData.map(sp);
            data.add(d);
        }
        Pager<List<EmployeeSpecializationData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(data);
        PageDetails details = new PageDetails();
        details.setPage(1);
        details.setPerPage(data.size());
        details.setTotalElements(Long.valueOf(data.size()));
        details.setTotalPage(data.size());
        details.setReportName("Specialization List");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/employee-specialization/{category}/category")
    @PreAuthorize("hasAuthority('view_employeeSpecialization')")
    public ResponseEntity<?> fetchAllEmployeeSpecializationsByCategory(@PathVariable("category") final EmployeeCategory.Category category) {

        List<EmployeeSpecialization> specialization = employeeSpecializationService.filterSpecializationsByCategory(category);
        List<EmployeeSpecializationData> data = new ArrayList<>();
        for (EmployeeSpecialization sp : specialization) {
            EmployeeSpecializationData d = EmployeeSpecializationData.map(sp);
            data.add(d);
        }
        Pager<List<EmployeeSpecializationData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(data);
        PageDetails details = new PageDetails();
        details.setPage(1);
        details.setPerPage(data.size());
        details.setTotalElements(Long.valueOf(data.size()));
        details.setTotalPage(data.size());
        details.setReportName("Specialization List");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/employee-specialization/{id}")
    @PreAuthorize("hasAuthority('view_employeeSpecialization')")
    public ResponseEntity<?> fetchEmployeeSpecializationById(@PathVariable("id") final Long id) {
        EmployeeSpecialization result = employeeSpecializationService.fetchSpecializationById(id);
        Pager<EmployeeSpecializationData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Specialization details identified by id " + id);
        pagers.setContent(EmployeeSpecializationData.map(result));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PutMapping("/employee-specialization/{id}")
    @PreAuthorize("hasAuthority('edit_employeeSpecialization')")
    public ResponseEntity<?> updateEmployeeSpecializationById(@Valid @RequestBody EmployeeSpecializationData d, @PathVariable("id") final Long id) {
        EmployeeSpecialization result = employeeSpecializationService.fetchSpecializationById(id);
        //EmployeeSpecialization es =  EmployeeSpecializationData.map(d);
        result.setCategory(d.getCategory());
        result.setSpecialization(d.getSpecialization());
        EmployeeSpecialization newResult = employeeSpecializationService.createEmployeeSpecialization(result);
        Pager<EmployeeSpecializationData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Specialization details identified by id " + id + " was successfully updated");
        pagers.setContent(EmployeeSpecializationData.map(newResult));

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @DeleteMapping("/employee-specialization/{id}")
    @PreAuthorize("hasAuthority('delete_employeeSpecialization')")
    public ResponseEntity<?> deleteEmployeeSpecializationById(@PathVariable("id") final Long id) {
        employeeSpecializationService.deleteSpecialization(id);
        return ResponseEntity.ok(ApiResponse.successMessage("Specialization details identified by id " + id + " was successfully deleted", HttpStatus.OK, id));
    }
}

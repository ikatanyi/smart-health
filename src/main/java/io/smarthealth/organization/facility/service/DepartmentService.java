/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.data.DepartmentData;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.DepartmentRepository;
import io.smarthealth.organization.facility.domain.Facility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
public class DepartmentService {

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    ModelMapper modelMapper;

    /*
    a. Create a new department
    b. Read all departments 
    c. Read department by Id
    c. Update department
     */
    @Transactional
    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public Page<Department> fetchAllDepartments(Pageable pgbl) {
        return departmentRepository.findAll(pgbl);
    }

    public Page<Department> fetchDepartmentByFacility(Facility facility, Pageable pgbl) {
        return departmentRepository.findByFacility(facility, pgbl);
    }

    public Department fetchDepartmentById(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> APIException.notFound("Department identified by {0} not found.", id));
    }

    public Department fetchDepartmentByCode(String code) {
        return departmentRepository.findByCode(code).orElseThrow(() -> APIException.notFound("Department identified by code {0} not found.", code));
    }

    public boolean departmentCodeExists(String code) {
        return departmentRepository.existsByCode(code);
    }

    public DepartmentData convertDepartmentToData(Department department) {
        DepartmentData departmentData = modelMapper.map(department, DepartmentData.class);
        return departmentData;
    }

}

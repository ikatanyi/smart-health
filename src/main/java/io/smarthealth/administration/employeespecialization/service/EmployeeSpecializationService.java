/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.employeespecialization.service;

import io.smarthealth.administration.employeespecialization.data.enums.EmployeeCategory;
import io.smarthealth.administration.employeespecialization.domain.EmployeeSpecialization;
import io.smarthealth.administration.employeespecialization.domain.EmployeeSpecializationRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
public class EmployeeSpecializationService {

    @Autowired
    EmployeeSpecializationRepository employeeSpecializationRepository;

    public EmployeeSpecialization createEmployeeSpecialization(final EmployeeSpecialization specialization) {
        return employeeSpecializationRepository.save(specialization);
    }

    public EmployeeSpecialization fetchSpecializationById(final Long id) {
        return employeeSpecializationRepository.findById(id).orElseThrow(() -> APIException.notFound("Speciailization identified by id {0} is not available ", id));
    }

    public List<EmployeeSpecialization> fetchAllSpecializations() {
        return employeeSpecializationRepository.findAll();
    }

    public List<EmployeeSpecialization> filterSpecializationsByCategory(final EmployeeCategory.Category category) {
        return employeeSpecializationRepository.findByCategory(category);
    }

    public void deleteSpecialization(final Long id) {
        employeeSpecializationRepository.deleteById(id);
    }

}

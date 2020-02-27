/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.employeespecialization.domain;

import io.smarthealth.administration.employeespecialization.data.enums.EmployeeCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Simon.waweru
 */
public interface EmployeeSpecializationRepository extends JpaRepository<EmployeeSpecialization, Long> {

    List<EmployeeSpecialization> findByCategory(final EmployeeCategory.Category category);
}

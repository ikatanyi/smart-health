/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface TestTypeRepository extends JpaRepository<LabTestType, Long> {

//    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN 'true' ELSE 'false' END FROM Employee e WHERE e.staffNumber = :staffNumber")
//    Boolean existsByStaffNumber(@Param("staffNumber") final String staffNumber);
//
    Optional<LabTestType> findByServiceCode(final String serviceCode);
    
//
//    Page<Employee> findByEmployeeCategory(final String categoryName, final Pageable pg);
}

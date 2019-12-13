/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.domain;

import io.smarthealth.auth.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Simon.waweru
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN 'true' ELSE 'false' END FROM Employee e WHERE e.staffNumber = :staffNumber")
    Boolean existsByStaffNumber(@Param("staffNumber") final String staffNumber);

    Optional<Employee> findByStaffNumber(final String staffNumber);

    Page<Employee> findByEmployeeCategory(final String categoryName, final Pageable pg);

    List<Employee> findAllByDepartment(Department department, Pageable pageable);

    List<Employee> findAllByEmployeeCategory(Employee.Category employeeCategory, Pageable pageable);

    Optional<Employee> findByLoginAccount(User user);
}

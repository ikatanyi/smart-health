/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.domain;

import io.smarthealth.administration.employeespecialization.data.enums.EmployeeCategory.Category;
import io.smarthealth.security.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<Employee> findAllByEmployeeCategory(Category employeeCategory, Pageable pageable);

    Optional<Employee> findByLoginAccount(User user);
    
    @Query("SELECT e FROM Employee e WHERE e.loginAccount.username =:username")
    Optional<Employee> findEmployeeBylogin(@Param("username") String username);
}

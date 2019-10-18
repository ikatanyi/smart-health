/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.domain;

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
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Page<Department> findByFacility(Facility facility, Pageable pageable);

    Optional<Department> findByCode(String code);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN 'true' ELSE 'false' END FROM Department d WHERE d.code = :code")
    Boolean existsByCode(@Param("code") final String code);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 *
 * @author Simon.waweru
 */
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Page<Department> findByFacility(Facility facility, Pageable pageable);

    Optional<Department> findByCode(String code);

    Optional<Department> findByServicePointTypeAndFacility(String servicePointType, Facility facility);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN 'true' ELSE 'false' END FROM Department d WHERE d.code = :code")
    Boolean existsByCode(@Param("code") final String code);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN 'true' ELSE 'false' END FROM Department d WHERE d.name = :name AND d.facility=:facility")
    Boolean existsByName(@Param("name") final String name, @Param("facility") final Facility facility);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN 'true' ELSE 'false' END FROM Department d WHERE d.name = :name AND d.facility=:facility AND d.servicePointType=:servicePointType")
    Boolean existsByNameAndServicePoint(@Param("name") final String name, @Param("facility") final Facility facility, @Param("servicePointType") final String servicePointType);
}

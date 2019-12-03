/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.domain;

import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.math.BigInteger;
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
public interface VisitRepository extends JpaRepository<Visit, Long> {

    Page<Visit> findByPatient(final Patient patient, Pageable page);

    Optional<Visit> findByVisitNumber(String visitNumber);

    Page<Visit> findByStatus(final VisitEnum.Status status, Pageable pageable);

    Optional<Visit> findByVisitNumberAndStatus(final String visitNumber, final String status);

    //Page<Visit> findByStatus(final String status, final Pageable pageable);
    Optional<Visit> findByPatientAndStatus(Patient patient, String status);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM Visit c WHERE c.status=:currentStatus AND  c.patient.patientNumber = :patient")
    Boolean visitExists(@Param("currentStatus") final String status, @Param("patient") final String patient);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM Visit c WHERE c.status='RUNNING' and c.visitNumber=:visitNumber")
    Boolean isVisitRunning(@Param("visitNumber") String visitNumber);

    @Query(value = "SELECT max(id) FROM Visit")
    public Integer maxVisitId();

}

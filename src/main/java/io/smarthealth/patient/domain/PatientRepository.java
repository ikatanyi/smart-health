/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.patient.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Simon.waweru
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM Patient c WHERE c.patientNumber = :patientNumber")
    Boolean existsByPatientNumber(@Param("patientNumber") final String patientNumber);

    Page<Patient> findByPatientNumberContainingOrGivenNameContainingOrSurnameContaining(
            final String patientNumber, final String givenName, final String surname, final Pageable pageable);

    Optional<Patient> findByPatientNumber(final String patientNumber);

    Page<Patient> findByStatus(final String currentStatus, final Pageable pageable);

}

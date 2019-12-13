/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.queue.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.person.patient.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * @author Simon.waweru
 */
@Repository
public interface PatientQueueRepository extends JpaRepository<PatientQueue, Long> {

    public Page<PatientQueue> findByDepartmentAndStatus(final Department department, final boolean status, final Pageable pageable);

    public Page<PatientQueue> findByPatient(Patient patient, Pageable pageable);

    Optional<PatientQueue> findByPatientAndDepartmentAndStatus(final Patient patient, final Department department, final boolean status);

    Optional<PatientQueue> findByVisit(Visit visit);
}

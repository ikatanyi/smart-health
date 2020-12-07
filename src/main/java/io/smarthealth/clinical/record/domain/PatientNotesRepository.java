/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Simon.waweru
 */
public interface PatientNotesRepository extends JpaRepository<PatientNotes, Long> {

    Page<PatientNotes> findByHealthProviderAndPatient(final Employee employee, Patient patient, final Pageable pageable);

    Page<PatientNotes> findByPatientOrderByDateRecordedDesc(Patient patient, final Pageable pageable);

    Page<PatientNotes> findByVisit(Visit visit, final Pageable pageable);

    Optional<PatientNotes> findByVisit(Visit visit);
}

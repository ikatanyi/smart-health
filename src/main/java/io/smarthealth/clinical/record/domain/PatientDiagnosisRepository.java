/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.organization.person.patient.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Simon.waweru
 */
public interface PatientDiagnosisRepository extends JpaRepository<PatientDiagnosis, Long>, JpaSpecificationExecutor<PatientDiagnosis> {

    Page<PatientDiagnosis> findByPatient(final Patient patient, Pageable page);

//    ContentPage<PatientTestsData> findByVisit(String visitNumber, Pageable page);
    Page<PatientDiagnosis> findByVisit(Visit visit, Pageable pageable);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.documents.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.organization.person.patient.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Simon.waweru
 */
@Repository
public interface TriageRepository extends JpaRepository<VitalsRecord, Long> {

    Page<VitalsRecord> findByVisit(final Visit visit, Pageable page);

    Page<VitalsRecord> findByPatient(final Patient patient, Pageable page);

}

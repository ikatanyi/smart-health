/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.pharmacy.domain;

import io.smarthealth.clinical.lab.domain.Testtype;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface PatientDrugsRepository extends JpaRepository<PatientDrugs, Long>{
    
    List<PatientDrugs> findByVisitOrPatient(final Visit visit, Patient patient);
}

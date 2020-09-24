/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.visit.data.enums.VisitEnum.Status;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Simon.waweru
 */
public interface AdmissionRepository extends JpaRepository<Admission, Long>, JpaSpecificationExecutor<Admission> {

    Optional<Admission> findByAdmissionNo(final String admissionNo);
    
    Optional<Admission>findByPatientAndStatus(final Patient patient, final Status status);
    
}

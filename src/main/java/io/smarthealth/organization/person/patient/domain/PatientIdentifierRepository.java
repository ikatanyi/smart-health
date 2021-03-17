/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Simon.waweru
 */
public interface PatientIdentifierRepository extends JpaRepository<PatientIdentifier, Long>, JpaSpecificationExecutor<PatientIdentifier> {

    List<PatientIdentifier> findByPatient(final Patient patient);

    Optional<PatientIdentifier> findByPatientAndId(final Patient patient, final Long Id);
}

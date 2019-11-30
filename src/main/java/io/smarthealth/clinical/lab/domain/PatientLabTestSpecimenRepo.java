/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Simon.Waweru
 */
public interface PatientLabTestSpecimenRepo extends JpaRepository<PatientLabTestSpecimen, Long> {

    List<PatientLabTestSpecimen> findByPatientLabTest(final PatientLabTest patientLabTest);
}

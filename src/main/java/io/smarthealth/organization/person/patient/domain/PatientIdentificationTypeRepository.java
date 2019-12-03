/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Simon.Waweru
 */
public interface PatientIdentificationTypeRepository extends JpaRepository<PatientIdentificationType, Long> {

}

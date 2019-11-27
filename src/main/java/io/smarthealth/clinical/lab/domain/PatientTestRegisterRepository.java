/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface PatientTestRegisterRepository extends JpaRepository<PatientTestRegister, Long> {

    @Query("SELECT pt FROM PatientTestRegister pt")
    Page<PatientTestRegister> findPatientTests(final Pageable pageable);
}

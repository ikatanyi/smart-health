/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface PatientTestRegisterRepository extends JpaRepository<PatientTestRegister, Long>, JpaSpecificationExecutor<PatientTestRegister> {

    @Query("SELECT pt FROM PatientTestRegister pt WHERE pt.visit.visitNumber=:visitNumber")
    Page<PatientTestRegister> findPatientTests(@Param("visitNumber") final String visitNo, final Pageable pageable);

    Optional<PatientTestRegister> findByAccessNo(final String accessNo);

}

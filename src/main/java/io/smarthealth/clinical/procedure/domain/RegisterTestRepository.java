/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.domain;

import io.smarthealth.clinical.radiology.domain.PatientScanTest;
import io.smarthealth.clinical.visit.domain.Visit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface RegisterTestRepository extends JpaRepository<PatientProcedureTest, Long>, JpaSpecificationExecutor<PatientProcedureTest> {
   @Query("FROM PatientProcedureTest d WHERE d.patientProcedureRegister.visit=:visit")
    List<PatientProcedureTest>findByVisit(@Param("visit")Visit visit);
}

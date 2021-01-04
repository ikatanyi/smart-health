/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.domain;

import io.smarthealth.clinical.radiology.domain.TotalTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface ProcedureTestRepository extends JpaRepository<PatientProcedureTest, Long>{
    @Modifying
    @Query("UPDATE PatientProcedureTest d SET d.paid=true WHERE d.id=:id")
    int updateProcedurePaid(@Param("id") Long id);

    @Query("SELECT d.procedureTest.itemName as testName, count(d.procedureTest.itemName) AS count, SUM(d.testPrice) as totalPrice FROM PatientProcedureTest d WHERE d.patientProcedureRegister.createdOn BETWEEN :fromDate AND :toDate Group by d.procedureTest.itemName")
    List<TotalTest> findTotalTests(@Param("fromDate") Instant fromDate, @Param("toDate")Instant toDate);

    @Query("SELECT d.procedureTest.itemName as testName, d.request.requestedBy as practitioner, count(d.procedureTest.itemName) AS count, SUM(d.testPrice) as totalPrice FROM PatientProcedureTest d WHERE d.patientProcedureRegister.createdOn BETWEEN :fromDate AND :toDate Group by d.request.requestedBy")
    List<TotalTest>findTotalTestsByPractitioner(@Param("fromDate")Instant fromDate, @Param("toDate")Instant toDate);
}

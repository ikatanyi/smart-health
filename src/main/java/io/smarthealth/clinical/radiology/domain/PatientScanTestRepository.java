/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface PatientScanTestRepository extends JpaRepository<PatientScanTest, Long>, JpaSpecificationExecutor<PatientScanTest>{
     @Modifying
    @Query("UPDATE PatientScanTest d SET d.paid=true WHERE d.id=:id")
    int updateImagingPaid(@Param("id") Long id);
    
    @Query("FROM PatientScanTest d WHERE d.patientScanRegister.visit.visitNumber=:visitNumber")
    List<PatientScanTest>findByVisit(@Param("visitNumber")String visitNumber);
    
    @Query("SELECT d.radiologyTest.scanName as testName, count(d.radiologyTest.scanName) AS count, SUM(d.testPrice) as totalPrice FROM PatientScanTest d WHERE d.createdOn BETWEEN :fromDate AND :toDate Group by d.radiologyTest")
    List<TotalTest>findTotalTests(@Param("fromDate")Instant fromDate, @Param("toDate")Instant toDate);
}

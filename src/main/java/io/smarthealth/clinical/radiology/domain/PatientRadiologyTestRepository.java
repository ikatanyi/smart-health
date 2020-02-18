/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface PatientRadiologyTestRepository extends JpaRepository<PatientScanRegister, Long>, JpaSpecificationExecutor<PatientScanRegister>{
    @Query("SELECT d FROM PatientScanRegister d WHERE d.visit=:visit")
    List<PatientScanRegister> findByVisit(@Param("visit") final Visit visit);    
     Optional<PatientScanRegister> findByAccessNo(final String accessNo);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import io.smarthealth.clinical.laboratory.domain.LabResult;
import io.smarthealth.clinical.visit.domain.Visit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface RadiologyResultRepository extends JpaRepository<RadiologyResult, Long>, JpaSpecificationExecutor<RadiologyResult>{
    @Query("FROM RadiologyResult l WHERE l.patientScanTest.patientScanRegister.visit = ?1")
    List<RadiologyResult> findByVisit(Visit visit);
}

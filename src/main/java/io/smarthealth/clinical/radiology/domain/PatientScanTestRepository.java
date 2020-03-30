/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

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
}

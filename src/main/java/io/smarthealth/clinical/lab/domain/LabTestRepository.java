/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface LabTestRepository extends JpaRepository<PatientLabTest, Long> {

     @Query("SELECT e FROM PatientLabTest e WHERE (:patientNumber=null OR e.patient.patientNumber = :patientNumber) AND (:visitNumber=null OR e.visit.visitNumber=:visitNumber) AND e.state=:status")
     Page<PatientLabTest>  findByPatientNumberAndVisitNumberAndStatus(@Param("visitNumber") final String visitNumber, @Param("patientNumber") final String patientNumber, @Param("status") final LabTestState status, Pageable pageable);
    
//    Page<Analyte> findByTestType(Testtype testtype, Pageable pageable);

//    Optional<Department> findByCode(String code);
}

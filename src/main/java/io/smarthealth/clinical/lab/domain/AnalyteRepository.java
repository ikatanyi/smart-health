/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface AnalyteRepository extends JpaRepository<Analyte, Long> {

    List<Analyte> findByTestType(LabTestType testtype);

    @Query("SELECT e FROM Analyte e WHERE e.testType = :testType AND (e.gender = :gender OR e.gender='Both') AND :age BETWEEN e.startAge and e.endAge")
    Page<Analyte> findAnalytesByGenderAndAge(LabTestType testType, String gender, Integer age, Pageable page);

    @Query("SELECT e FROM Analyte e WHERE e.testType = :testType AND (e.gender = :gender OR e.gender='Both') AND :agesize BETWEEN e.startAge and e.endAge")
    List<Analyte> findAllAnalyteByPatientsAndTests(@Param("testType") final LabTestType testType, @Param("gender") Analyte.Gender gender, @Param("agesize") int age);
}

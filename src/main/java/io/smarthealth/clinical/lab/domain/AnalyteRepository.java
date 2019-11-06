package io.smarthealth.clinical.lab.domain;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface AnalyteRepository extends JpaRepository<Analyte, Long> {
    
    Page<Analyte> findByTestType(LabTestType testtype, Pageable pageable);
    
    @Query("SELECT e FROM Analyte e WHERE e.testType = :testType AND e.gender = :gender AND :age BETWEEN e.startAge and e.endAge")
    List<Analyte> findAnalytebyage(LabTestType testType, String gender, Integer age);
    
    
}

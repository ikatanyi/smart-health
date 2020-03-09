package io.smarthealth.clinical.laboratory.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface AnalyteRepository extends JpaRepository<Analyte, Long>{
    
}

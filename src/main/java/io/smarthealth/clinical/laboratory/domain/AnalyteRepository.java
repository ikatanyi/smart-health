package io.smarthealth.clinical.laboratory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface AnalyteRepository extends JpaRepository<Analyte, Long> {

    @Modifying
    @Query("delete from Analyte a WHERE a.labTest.id =:testId")
    int deleteByTestId(@Param("testId") Long testId);

}

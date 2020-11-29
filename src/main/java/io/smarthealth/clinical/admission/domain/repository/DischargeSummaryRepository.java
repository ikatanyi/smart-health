/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.DischargeSummary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface DischargeSummaryRepository extends JpaRepository<DischargeSummary, Long>, JpaSpecificationExecutor<DischargeSummary> {

    Optional<DischargeSummary> findByDischargeNo(String dischargNo);

    Optional<DischargeSummary> findByAdmission(Admission admission);
    
    @Query("SELECT d FROM DischargeSummary d WHERE d.admission.visitNumber =:visitNo")
    Optional<DischargeSummary> findDischargeByVisitNo(@Param("visitNo") String visitNo);
}

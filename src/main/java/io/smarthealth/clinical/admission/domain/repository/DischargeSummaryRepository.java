/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.DischargeSummary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface DischargeSummaryRepository extends JpaRepository<DischargeSummary, Long>, JpaSpecificationExecutor<DischargeSummary> {
    Optional<DischargeSummary>findByDischargeNo(String dischargNo);
}

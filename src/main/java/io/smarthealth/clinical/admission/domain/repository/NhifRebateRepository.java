/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.NhifRebate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kent
 */
public interface NhifRebateRepository extends JpaRepository<NhifRebate, Long>, JpaSpecificationExecutor<NhifRebate> {
    Optional<NhifRebate>findByAdmission(Admission admission);
}

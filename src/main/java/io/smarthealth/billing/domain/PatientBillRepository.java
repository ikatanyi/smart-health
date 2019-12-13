package io.smarthealth.billing.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface PatientBillRepository extends JpaRepository<PatientBill, Long>, JpaSpecificationExecutor<PatientBill> {
    
    Optional<PatientBill> findByBillNumber(final String identifier);
     
}

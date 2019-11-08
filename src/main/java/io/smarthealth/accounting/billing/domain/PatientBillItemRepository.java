package io.smarthealth.accounting.billing.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface PatientBillItemRepository extends JpaRepository<PatientBillItem, Long>{
    
}

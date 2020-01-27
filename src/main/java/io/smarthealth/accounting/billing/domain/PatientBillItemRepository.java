package io.smarthealth.accounting.billing.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface PatientBillItemRepository extends JpaRepository<PatientBillItem, Long>{
    
}

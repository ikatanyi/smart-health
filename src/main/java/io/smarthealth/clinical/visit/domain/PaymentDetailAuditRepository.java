package io.smarthealth.clinical.visit.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface PaymentDetailAuditRepository extends JpaRepository<PaymentDetailAudit, Long> {
    
}

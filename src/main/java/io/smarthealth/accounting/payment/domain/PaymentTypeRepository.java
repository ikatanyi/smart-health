package io.smarthealth.accounting.payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
    
 */
@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long>{
    
}

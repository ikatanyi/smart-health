package io.smarthealth.administration.app.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
    
 */
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long>{
    
}

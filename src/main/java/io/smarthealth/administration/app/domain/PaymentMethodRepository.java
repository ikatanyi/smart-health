package io.smarthealth.administration.app.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * @author Kelsas
    
 */
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long>{
    Optional<PaymentMethod> findByName(String name);
}

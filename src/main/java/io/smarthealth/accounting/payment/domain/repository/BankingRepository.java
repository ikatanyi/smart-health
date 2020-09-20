package io.smarthealth.accounting.payment.domain.repository;

import io.smarthealth.accounting.payment.domain.Banking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface BankingRepository extends JpaRepository<Banking, Long>, JpaSpecificationExecutor<Banking>{
    
}

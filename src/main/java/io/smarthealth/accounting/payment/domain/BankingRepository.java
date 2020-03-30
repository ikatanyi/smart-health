package io.smarthealth.accounting.payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface BankingRepository extends JpaRepository<Banking, Long>, JpaSpecificationExecutor<Banking>{
    
}

package io.smarthealth.accounting.invoice.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface DebtorsRepository extends JpaRepository<Debtors, Long>{
    
}

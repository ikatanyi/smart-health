package io.smarthealth.billing.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface BillItemRepository extends JpaRepository<BillItem, Long>{
    
}

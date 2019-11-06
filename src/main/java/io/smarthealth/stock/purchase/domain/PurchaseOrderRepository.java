package io.smarthealth.stock.purchase.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>{
    
}

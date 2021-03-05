package io.smarthealth.stock.purchase.domain;

import io.smarthealth.stock.purchase.domain.enumeration.PurchaseOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * @author Kelsas
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>, JpaSpecificationExecutor<PurchaseOrder> {

    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);

    Page<PurchaseOrder> findByStatus(PurchaseOrderStatus status, Pageable page);
    
    
}

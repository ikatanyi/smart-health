package io.smarthealth.stock.purchase.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {

    @Modifying
    @Query("update PurchaseOrderItem p set p.receivedQuantity=:qty where p.id=:id")
    int updateReceivedQuantity(@Param("qty") Double qty, @Param("id") Long id);
}

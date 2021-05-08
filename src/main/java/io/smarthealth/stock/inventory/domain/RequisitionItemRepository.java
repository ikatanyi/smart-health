package io.smarthealth.stock.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 *
 * @author Kelsas
 */
@Repository
public interface RequisitionItemRepository extends JpaRepository<RequisitionItem, Long>{

    @Transactional
    @Modifying
    @Query("update RequisitionItem i SET i.receivedQuantity = (i.receivedQuantity+ :qty) where i.requistion.id = :requisitionId and i.id = :id")
    void updateRequisitionReceivedQuantity(double qty, Long requisitionId, Long id);
}

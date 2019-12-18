package io.smarthealth.stock.inventory.domain;

import io.smarthealth.stock.item.domain.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>, JpaSpecificationExecutor<InventoryItem> {
     Optional<InventoryItem> findByItem(Item item);
}

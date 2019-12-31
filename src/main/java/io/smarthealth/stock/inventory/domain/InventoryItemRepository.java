package io.smarthealth.stock.inventory.domain;

import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>, JpaSpecificationExecutor<InventoryItem> {

    Page<InventoryItem> findByItem(Item item, Pageable page);

    Page<InventoryItem> findByStore(Store store, Pageable page);

    Optional<InventoryItem> findByItemAndStore(Item item, Store store);

}

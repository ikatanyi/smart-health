package io.smarthealth.stock.inventory.domain;

import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>, JpaSpecificationExecutor<InventoryItem> {

    Page<InventoryItem> findByItem(Item item, Pageable page);
    
    @Query(value = "select SUM(availableStock) as cnt from InventoryItem v where v.item=:item group by v.item")
    Double findItemCount(@Param("item")Item item);        

    Page<InventoryItem> findByStore(Store store, Pageable page);

    Optional<InventoryItem> findByItemAndStore(Item item, Store store);

    List<InventoryItem> findByStoreAndItemIn(Store store, Collection<Item> items);

}

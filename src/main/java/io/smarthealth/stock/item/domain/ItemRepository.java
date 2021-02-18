package io.smarthealth.stock.item.domain;

import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author kelsas
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    Optional<Item> findByItemCode(final String itemCode);

    Optional<Item> findByItemName(final String itemName);

    Page<Item> findByCategory(final ItemCategory itemCode, final Pageable pageable);

    Optional<Item> findFirstByCategory(ItemCategory category);
    
    List<Item> findByActiveTrue(); 

    List<Item> findByCategoryAndActiveTrue(ItemCategory category);  //
    
    List<Item> findByItemTypeAndActiveTrue(ItemType itemType);
}

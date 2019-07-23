package io.smarthealth.stock.item.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author kelsas
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long>{
    
}

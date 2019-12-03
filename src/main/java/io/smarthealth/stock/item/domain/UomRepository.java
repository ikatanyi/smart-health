package io.smarthealth.stock.item.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface UomRepository extends JpaRepository<Uom, Long>{
    
}

package io.smarthealth.stock.item.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Simon.waweru
 */
@Repository
public interface UomRepository extends JpaRepository<Uom, Long>{
    
}

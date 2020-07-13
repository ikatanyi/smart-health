package io.smarthealth.stock.item.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface UomRepository extends JpaRepository<Uom, Long>{
    List<Uom> findByNameContainingIgnoreCase(String name);
}

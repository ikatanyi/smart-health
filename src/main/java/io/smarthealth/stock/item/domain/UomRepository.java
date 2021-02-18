package io.smarthealth.stock.item.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Kelsas
 */
@Repository
public interface UomRepository extends JpaRepository<Uom, Long>{
    List<Uom> findByNameContainingIgnoreCase(String name);
}

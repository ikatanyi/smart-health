package io.smarthealth.stock.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Simon.waweru
 */
public interface ReorderRuleRepository extends JpaRepository<ReorderRule, Long> {

}

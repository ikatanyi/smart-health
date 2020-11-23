/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import io.smarthealth.stock.item.domain.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface RadiologyRepository extends JpaRepository<RadiologyTest, Long>,JpaSpecificationExecutor<RadiologyTest>{
    Optional<RadiologyTest> findByItem(final Item item);
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.domain;

import io.smarthealth.stock.item.domain.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface ProcedureRepository extends JpaRepository<ProcedureTest, Long>{
    Optional<ProcedureTest> findByItem(final Item item);
    
}

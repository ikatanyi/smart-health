package io.smarthealth.clinical.procedure.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface ProcedureConfigurationRepository extends JpaRepository<ProcedureConfiguration, Long>, JpaSpecificationExecutor<ProcedureConfiguration>{
    
    @Query("SELECT p FROM ProcedureConfiguration p WHERE p.procedure.id =:itemId")
    List<ProcedureConfiguration> findConfigByItem(@Param("itemId") Long itemId);
}

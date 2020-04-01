package io.smarthealth.clinical.pharmacy.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface DispensedDrugRepository extends JpaRepository<DispensedDrug, Long>, JpaSpecificationExecutor<DispensedDrug> {

    @Modifying
    @Query("UPDATE DispensedDrug d SET d.paid=true WHERE d.id=:id")
    int updateDrugPaid(@Param("id") Long id);
}

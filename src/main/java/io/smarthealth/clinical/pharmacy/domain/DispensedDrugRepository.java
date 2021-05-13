package io.smarthealth.clinical.pharmacy.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.smarthealth.accounting.billing.domain.PatientBillItem;
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

    Optional<DispensedDrug> findDispensedDrugByBillItem(PatientBillItem item);

    @Modifying
    @Query("UPDATE DispensedDrug d SET d.paid=true WHERE d.id=:id")
    int updateDrugPaid(@Param("id") Long id);

    @Query("SELECT d.drug.itemCode AS itemId,  d.dispensedDate, d.drug.itemName AS drug, SUM(d.qtyIssued) AS  qty, d.price AS price, d.drug.rate AS cost, d.otherReference as otherReference FROM  DispensedDrug d WHERE d.dispensedDate BETWEEN :fromDate AND :toDate GROUP BY d.drug")
    List<DispensedDrugsInterface> dispensedDrugs(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT d FROM DispensedDrug d WHERE d.drug.id=:drugId AND d.visit.visitNumber = :visitNo AND d.dispensedDate = :dispensedDate AND d.transactionId = :transactionId ")
    List<DispensedDrug> findDispensedDrug(@Param("drugId") Long drugId, @Param("visitNo") String visitNo, @Param("dispensedDate") LocalDate date, @Param("transactionId") String transNo);
}

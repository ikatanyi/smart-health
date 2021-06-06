package io.smarthealth.accounting.billing.domain;

import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import java.util.List;
import java.util.Optional;

import io.smarthealth.stock.item.domain.Item;
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
public interface PatientBillItemRepository extends JpaRepository<PatientBillItem, Long>, JpaSpecificationExecutor<PatientBillItem>, BillRepository {

    @Query(value = "SELECT p FROM PatientBillItem p WHERE p.status=:status AND p.patientBill.visit.visitNumber=:visitNo")
    List<PatientBillItem> getVisitBillsByStatus(@Param("status") BillStatus status, @Param("visitNo") String visitNo);

    Optional<PatientBillItem> findByPatientBill(PatientBill patientBill);

    Optional<PatientBillItem> findByPatientBillAndItem(final PatientBill patientBill, final Item item);

    List<PatientBillItem> findByTransactionId(String transactionId);

    @Modifying
    @Query(value = "UPDATE PatientBillItem p SET p.status = 'Canceled' WHERE p.id = :id")
    int cancelPatientBill(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE PatientBillItem p SET p.paymentReference =:newRef WHERE p.paymentReference=:oldRef ")
    int updatePaymentReference(@Param("newRef") String newRef, @Param("oldRef") String oldRef);

    @Query(value = "SELECT p FROM PatientBillItem p WHERE p.patientBill.visit.visitNumber=:visitNo")
    List<PatientBillItem> getByVisitNumber(@Param("visitNo") String visitNo);

    @Query(value = "SELECT p FROM PatientBillItem p WHERE p.status <> 'Canceled' AND p.finalized = false AND p.patientBill.visit.visitNumber=:visitNo group by p.servicePoint, p.id order by p.servicePoint, p.billingDate")
    List<PatientBillItem> getByVisitNumberStatus(@Param("visitNo") String visitNo);

    @Query(value = "SELECT p FROM PatientBillItem p WHERE p.status <> 'Canceled' AND p.finalized = false AND p.patientBill.visit.visitNumber=:visitNo order by p.billingDate, p.id")
    List<PatientBillItem> getByBillingDate(@Param("visitNo") String visitNo);

    Optional<PatientBillItem> getPatientBillItemByPaymentReference(String reference);


}

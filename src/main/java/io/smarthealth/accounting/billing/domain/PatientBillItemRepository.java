package io.smarthealth.accounting.billing.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface PatientBillItemRepository extends JpaRepository<PatientBillItem, Long>, JpaSpecificationExecutor<PatientBillItem>, BillSummaryRepository, BillRepository {

//    @Deprecated
//    @Query(value = "SELECT p from PatientBillItem p WHERE p.patientBill.visit=:visit")
//    Page<PatientBillItem> findPatientBillItemByVisit(Visit visit, Pageable page);

//    @Query("SELECT b.patientBill.billingDate as date, b.patientBill.visit.visitNumber as visitNumber, b.patientBill.patient.patientNumber as patientNumber, b.patientBill.patient.fullName as patientName, SUM(b.amount) as amount, SUM(b.balance) as balance, b.patientBill.visit.paymentMethod as paymentMethod, 'False' as walkin FROM PatientBillItem b GROUP BY 2,3 ")
//    Page<BillSummary> billSummary(Pageable page); 
//
//    @Query("SELECT b.patientBill.billingDate as date, b.patientBill.visit.visitNumber as visitNumber, b.patientBill.patient.patientNumber as patientNumber, b.patientBill.patient.fullName as patientName, SUM(b.amount) as amount, SUM(b.balance) as balance, b.patientBill.visit.paymentMethod as paymentMethod, 'False' as walkin FROM PatientBillItem b WHERE b.patientBill.visit.visitNumber =:visit GROUP BY 2,3")
//    Page<BillSummary> billSummaryByVisit(@Param("visit") String visitNumber, Pageable page);
}
